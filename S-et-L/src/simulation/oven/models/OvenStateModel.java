package simulation.oven.models;

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
import simulation.oven.events.OvenStateEvent;
import simulation.oven.events.OvenSwitchEvent;
import utils.oven.OvenMode;

/**
 * The class <code>OvenSateModel</code> describes the evolution of a Oven State 
 * @author Saad CHIADMI
 *
 */
@ModelExternalEvents(imported = {OvenSwitchEvent.class, EconomyEvent.class, NoEconomyEvent.class})
public class OvenStateModel 
extends AtomicHIOAwithEquations{

	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	
	public static class OvenStateModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<OvenStateEvent>	readings ;

		public			OvenStateModelReport(
			String modelURI,
			Vector<OvenStateEvent> readings
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
			ret += "Oven State Model Report\n" ;
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
	

	public OvenStateModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.setDebugLevel(1);
		states = new Vector<OvenStateEvent>();
		assert this.temperature != null;
		this.staticInitialiseVariables();
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Oven-STATE";
	
	/** stored output events for report */
	protected Vector<OvenStateEvent> states;
	/** current state of the OVEN (On, Off) */
	protected OvenMode currentState;
	
	private static final String	SERIES_STATE = "Oven state" ;
	
	public static final String OVENSTATE_PLOTTING_PARAM_NAME = "oven-state-plot";
	
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter			statePlotter ;
	
	/** default value of oven temperature */
	public static double DEFAULT_OVEN_TEMPERATURE = 70.0;
	
	/** max value of temperature when mode economy activated */
	public static double MAX_ECO_TEMPERATURE = 30.0;
	
	/** Last value of temperature when oven was on	 */
	private double last_value_temperature;
	
	/** true if controller have actived energy economy */
	protected boolean modeEco;
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** OvenConsumption in Watt.								*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>		temperature =
											new Value<Double>(this, 0.0, 0) ;
	
	
	/**
	 * return an integer for the state of the Oven for plot
	 * @param mode state of Oven
	 * @return
	 */
	public static int	state2int(OvenMode mode) {
		assert mode != null;
		if(mode == OvenMode.On) return 1;
		else {
			assert mode == OvenMode.Off;
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
		
	String stateName = this.getURI() + ":" +
			OVENSTATE_PLOTTING_PARAM_NAME ;
	PlotterDescription pdState = (PlotterDescription) simParams.get(stateName) ;
	this.statePlotter = new XYPlotter(pdState) ;
	this.statePlotter.createSeries(SERIES_STATE) ;
	
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = OvenMode.Off;
		this.modeEco = false;
		this.last_value_temperature = DEFAULT_OVEN_TEMPERATURE;
		if (this.statePlotter != null) {
			this.statePlotter.initialise() ;
			this.statePlotter.showPlotter() ;
		}
		super.initialiseState(initialTime);
		if (this.statePlotter != null) {
			this.statePlotter.addData(
				SERIES_STATE,
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
		//super.initialiseState(startTime);
		this.temperature.v = 0.0;
		assert	startTime.equals(this.temperature.time) ;
		
//		this.temperature.v = 0.0;
//		super.initialiseState(startTime);
//		if (this.statePlotter != null) {
//			this.statePlotter.addData(
//				SERIES_STATE,
//				startTime.getSimulatedTime(),
//				state2int(this.currentState)) ;
//		}
		
	}
	
//	/**
//	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
//	 */
//	@Override
//	public void			userDefinedInternalTransition(Duration elapsedTime)
//	{
//		this.logMessage("at internal transition " +
//				this.getCurrentStateTime().getSimulatedTime() +
//				" " + elapsedTime.getSimulatedDuration()) ;
//
//		if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
//		super.userDefinedInternalTransition(elapsedTime) ;
//		if (this.currentState == OvenMode.On) {
//			// the value of the bandwidth at the next internal transition
//			// is computed in the timeAdvance function when computing
//			// the delay until the next internal transition.
//			this.temperature.v = 30.0 ;
//		}
//		this.temperature.time = this.getCurrentStateTime() ;
//		}
//	}
	
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
		if(e instanceof OvenSwitchEvent) {
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
//		Vector<EventI> ret = new Vector<EventI>();
//		if(receivedEvent) {
//			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
//			OvenStateEvent e = new OvenStateEvent(t,currentState);
//			states.addElement(e);
//			this.logMessage(e.eventAsString());
//			receivedEvent = false;
//		}
//		return ret;
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return Duration.INFINITY;
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new OvenStateModelReport(this.getURI(),states);
	}
	
	private void switchState(double currentTime) {
		OvenMode oldState = this.currentState;
		if(oldState == OvenMode.Off) {
			currentState = OvenMode.On;
			temperature.v = this.last_value_temperature;
		}
		else
		{
			assert oldState == OvenMode.On;
			currentState = OvenMode.Off;
			last_value_temperature = temperature.v;
			temperature.v = 0.0;
			
		} 
		
		temperature.time = this.getCurrentStateTime();
		
		if (this.statePlotter != null && oldState != this.currentState) {
			this.statePlotter.addData(
					SERIES_STATE,
					currentTime,
					state2int(oldState)) ;
			this.statePlotter.addData(
					SERIES_STATE,
					currentTime,
					state2int(this.currentState)) ;
		}
		
	}
	
	private void activateEnergyEco() {
		this.modeEco = true;
		if(temperature.v > MAX_ECO_TEMPERATURE) {
			temperature.v = MAX_ECO_TEMPERATURE;
			temperature.time = this.getCurrentStateTime();
		}
		else if(this.currentState == OvenMode.Off){
			this.last_value_temperature = MAX_ECO_TEMPERATURE;
		}
	}
	
	private void deactivateEnergyEco() {
		this.modeEco = false;
		if(temperature.v < DEFAULT_OVEN_TEMPERATURE && this.currentState == OvenMode.On) {
			temperature.v = DEFAULT_OVEN_TEMPERATURE;
			temperature.time = this.getCurrentStateTime();
		}
		else {
			this.last_value_temperature = DEFAULT_OVEN_TEMPERATURE;
		}
		
	}

}
