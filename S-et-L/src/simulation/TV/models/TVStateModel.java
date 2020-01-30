package simulation.TV.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.TV.events.TVSwitch;
import simulation.TV.events.TvStateEvent;
import utils.TVMode;

/**
 * The class <code>TVStateModel</code> describes the evolution of a TV State 
 * @author Laurie Rakotoarisoa
 *
 */

@ModelExternalEvents(imported = {TVSwitch.class, EconomyEvent.class, NoEconomyEvent.class})
public class TVStateModel 
extends AtomicHIOAwithEquations{
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	
	public static class TVStateModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<TvStateEvent>	readings ;

		public			TVStateModelReport(
			String modelURI,
			Vector<TvStateEvent> readings
			)
		{
			super(modelURI) ;
			this.readings = readings ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "TV State Model Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of changes = " + this.readings.size() + "\n" ;
			ret += "Changes of state :\n" ;
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public TVStateModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.setDebugLevel(1);
		states = new Vector<TvStateEvent>();
		assert this.tvBack != null;
		this.staticInitialiseVariables();
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "TV-STATE";
	
	/** stored output events for report */
	protected Vector<TvStateEvent> states;
	
	/** current state of the TV (On, Off) */
	protected TVMode currentState;
	
	private static final String	SERIES1 = "TV state" ;
	
	public static final String TVSTATE_PLOTTING_PARAM_NAME = "tv-state-plot";
	
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter			statePlotter ;
	
	/** default value of tv backlight when mode economy is not activated */
	public static double DEFAULT_TV_BACKLIGHT = 70.0;
	
	/** maximum value of backlight when mode economy activated */
	public static double MAX_ECO_BACKLIGHT = 30.0;
	
	/** Last value of backlight when TV was on	 */
	private double last_value_backlight;
	
	/** true if controller have actived energy economy */
	protected boolean modeEco;
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** TVConsumption in Watt.								*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>		tvBack =
											new Value<Double>(this, 0.0, 0) ;
	
	
	/**
	 * return an integer for the state of the TV for plot
	 * @param mode state of TV 
	 * @return
	 */
	public static int	state2int(TVMode mode) {
		assert mode != null;
		if(mode == TVMode.On) return 1;
		else {
			assert mode == TVMode.Off;
			return 0;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		
		String vname = this.getURI() + ":" +
				TVSTATE_PLOTTING_PARAM_NAME ;
		if(simParams.get(vname) != null) {
			PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
			this.statePlotter = new XYPlotter(pd) ;
			this.statePlotter.createSeries(SERIES1) ;
		}
	
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = TVMode.Off;
		this.modeEco = false;
		this.last_value_backlight = DEFAULT_TV_BACKLIGHT;
		if (this.statePlotter != null) {
			this.statePlotter.initialise() ;
			this.statePlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
		if (this.statePlotter != null) {
			this.statePlotter.addData(
				SERIES1,
				initialTime.getSimulatedTime(),
				state2int(this.currentState)) ;
		}
		
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);
		this.tvBack.v = 0.0;
		assert	startTime.equals(this.tvBack.time) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;
		ArrayList<EventI> current = this.getStoredEventAndReset();
		assert current != null & current.size() == 1;
		EventI e = current.get(0);
		if(e instanceof TVSwitch) {
			switchState(this.getCurrentStateTime().getSimulatedTime());
			
		}
		else if(e instanceof EconomyEvent) {
			if(!modeEco) activateEnergyEco();
		}
		else if(e instanceof NoEconomyEvent) {
			if(modeEco) deactivateEnergyEco();
		}			
		
	}
	
	@Override
	public ArrayList<EventI> output() {
		return null;
		
	}

	@Override
	public Duration timeAdvance() {
		return Duration.INFINITY;
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new TVStateModelReport(this.getURI(),states);
	}
	
	private void switchState(double currentTime) {
		TVMode oldState = this.currentState;
		if(oldState == TVMode.Off) {
			currentState = TVMode.On;
			tvBack.v = this.last_value_backlight;
		}
		else
		{
			assert oldState == TVMode.On;
			currentState = TVMode.Off;
			last_value_backlight = tvBack.v;
			tvBack.v = 0.0;
			
		} 
		
		tvBack.time = this.getCurrentStateTime();
		
		if (this.statePlotter != null && oldState != this.currentState) {
			this.statePlotter.addData(
					SERIES1,
					currentTime,
					state2int(oldState)) ;
			this.statePlotter.addData(
					SERIES1,
					currentTime,
					state2int(this.currentState)) ;
		}
		
	}
	
	private void activateEnergyEco() {
		this.modeEco = true;
		if(tvBack.v > MAX_ECO_BACKLIGHT) {
			tvBack.v = MAX_ECO_BACKLIGHT;
			tvBack.time = this.getCurrentStateTime();
		}
		else if(this.currentState == TVMode.Off){
			this.last_value_backlight = MAX_ECO_BACKLIGHT;
		}
	}
	
	private void deactivateEnergyEco() {
		this.modeEco = false;
		if(tvBack.v < DEFAULT_TV_BACKLIGHT && this.currentState == TVMode.On) {
			tvBack.v = DEFAULT_TV_BACKLIGHT;
			tvBack.time = this.getCurrentStateTime();
		}
		else {
			this.last_value_backlight = DEFAULT_TV_BACKLIGHT;
		}
		
	}
	
	
	
	

}
