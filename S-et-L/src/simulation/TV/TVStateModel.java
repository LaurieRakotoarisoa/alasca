package simulation.TV;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
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
import simulation.AtomicModels.events.TvStateEvent;
import simulation.TV.events.TVSwitch;
import utils.TVMode;

/**
 * The class <code>TVStateModel</code> describes the evolution of a TV State 
 * @author Laurie Rakotoarisoa
 *
 */

@ModelExternalEvents(imported = {TVSwitch.class},
					exported = {TvStateEvent.class})
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
		receivedEvent = false;
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
		states = new Vector<TvStateEvent>();
		assert this.tvBack != null;
		this.staticInitialiseVariables();
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "TV-STATE";
	
	protected boolean receivedEvent;
	
	/** stored output events for report */
	protected Vector<TvStateEvent> states;
	
	protected TVMode currentState;
	
	private static final String	SERIES1 = "TV state" ;
	
	public static final String TVSTATE_PLOTTING_PARAM_NAME = "tv-state-plot";
	
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter			statePlotter ;
	
	/** default value of tv backlight */
	public static double DEFAULT_TV_BACKLIGHT = 70.0;
	
	/** max value of backlight when mode economy activated */
	public static double MAX_ECO_BACKLIGHT = 30.0;
	
	/** Last value of backlight when TV was on	 */
	private double last_value_backlight;
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** TVConsumption in Watt.								*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>		tvBack =
											new Value<Double>(this, 12.0, 0) ;
	
	
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
	PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
	this.statePlotter = new XYPlotter(pd) ;
	this.statePlotter.createSeries(SERIES1) ;
	
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = TVMode.Off;
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
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.logMessage("at internal transition " +
							this.getCurrentStateTime().getSimulatedTime() +
							" " + elapsedTime.getSimulatedDuration()) ;
		
		if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
			super.userDefinedInternalTransition(elapsedTime) ;
			if (this.currentState == TVMode.On) {
				// the value of the bandwidth at the next internal transition
				// is computed in the timeAdvance function when computing
				// the delay until the next internal transition.
				this.tvBack.v = 13.0 ;
			}
			this.tvBack.time = this.getCurrentStateTime() ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;
		Vector<EventI> current = this.getStoredEventAndReset();
		assert current != null & current.size() == 1;
		if(current.get(0) instanceof TVSwitch) { 
			TVMode oldState = this.currentState;
			switchState();
			receivedEvent = true;
			if (this.statePlotter != null && oldState != this.currentState) {
				this.statePlotter.addData(
						SERIES1,
						this.getCurrentStateTime().getSimulatedTime(),
						state2int(oldState)) ;
				this.statePlotter.addData(
						SERIES1,
						this.getCurrentStateTime().getSimulatedTime(),
						state2int(this.currentState)) ;
			}
		}
			
			
		
	}
	
	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = new Vector<EventI>();
		if(receivedEvent) {
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
			TvStateEvent e = new TvStateEvent(t,currentState);
			states.addElement(e);
			this.logMessage(e.eventAsString());
			receivedEvent = false;
		}
		return ret;
	}

	@Override
	public Duration timeAdvance() {
		return new Duration(30.0, TimeUnit.SECONDS);
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new TVStateModelReport(this.getURI(),states);
	}
	
	private void switchState() {
		if(currentState == TVMode.Off) {
			currentState = TVMode.On;
			tvBack.v = this.last_value_backlight;
		}
		else
		{
			assert currentState == TVMode.On;
			currentState = TVMode.Off;
			last_value_backlight = tvBack.v;
			tvBack.v = 0.0;
			
		} 
		tvBack.time = this.getCurrentStateTime();
	}
	
	
	
	

}
