package clean.equipments.oven.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import components.device.Oven;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
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

@ModelExternalEvents (imported = {NoEconomyEvent.class,
								EconomyEvent.class,
								OvenSwitchEvent.class})	
public class OvenStateMILModel 
extends AtomicHIOA
implements SGMILModelImplementationI{
	
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
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public OvenStateMILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setDebugLevel(2);
		states = new Vector<OvenStateEvent>();
		//assert this.temperature != null;
		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void		finalize() throws Throwable
	{
		if (this.statePlotter != null) {
			this.statePlotter.dispose() ;
		}
		super.finalize();
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	public static final String URI = OvenStateMILModel.class.getName();
	
	public static final String		COMPONENT_HOLDER_REF_PARAM_NAME =
			"oven state component reference" ;
	
	/** stored output events for report */
	protected Vector<OvenStateEvent> states;
	
	/** current state of the Oven (On, Off) */
	protected OvenMode currentState;
	
	private static final String	SERIES = "Oven state" ;
	
	public static final String OvenSTATE_PLOTTING_PARAM_NAME = "oven-state-plot";
	
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter			statePlotter ;
	
	/** default value of oven temperature when mode economy is not activated */
	public static int DEFAULT_OVEN_TEMPERATURE = 170;
	
	/** maximum value of temperature when mode economy activated */
	public static int MAX_ECO_TEMPERATURE = 70;
	
	/** Last value of temperature when Oven was on	 */
	private double last_value_temperature;
	
	/** true if controller have actived energy economy */
	protected boolean modeEco;
	
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentAccessI componentRef ;
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** OvenConsumption in Watt.								*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>		temperature =
											new Value<Double>(this, 0.0, 0) ;
	
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
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
		
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentAccessI)
							simParams.get(COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = OvenMode.Off;
		this.modeEco = false;
		this.last_value_temperature = DEFAULT_OVEN_TEMPERATURE;

		PlotterDescription pd =
				new PlotterDescription(
						"Oven State Model",
						"Time (sec)",
						"Intensity (Amp)",
						100,
						0,
						600,
						400) ;
		this.statePlotter = new XYPlotter(pd) ;
		this.statePlotter.createSeries(SERIES) ;
		
		this.statePlotter.initialise() ;
		this.statePlotter.showPlotter() ;
		
		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
		super.initialiseState(initialTime);

		
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		this.temperature.v = 0.0;
		this.statePlotter.addData(
				SERIES,
				startTime.getSimulatedTime(),
				state2int(this.currentState)) ;
		System.out.println("init");
		super.initialiseVariables(startTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		
		super.userDefinedInternalTransition(elapsedTime);
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
		e.executeOn(this);
		
		
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.statePlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				state2int(this.currentState)) ;

		super.endSimulation(endTime) ;
	}
	
	@Override
	public ArrayList<EventI> output() {
		return null;
		
	}

	@Override
	public Duration timeAdvance() {
		if(this.componentRef == null) return Duration.INFINITY;
		else return new Duration(10.0, TimeUnit.SECONDS);
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new OvenStateModelReport(this.getURI(),states);
	}
	
	public void switchState(double currentTime) {
		OvenMode oldState = this.currentState;
		if(oldState == OvenMode.Off) {
			currentState = OvenMode.On;
			temperature.v = this.last_value_temperature;
		}
		else
		{
			//assert oldState == OvenMode.On;
			currentState = OvenMode.Off;
			last_value_temperature = temperature.v;
			temperature.v = 0.0;
		} 
		
		temperature.time = this.getCurrentStateTime();
		
		this.statePlotter.addData(
				SERIES,
				currentTime,
				state2int(oldState)) ;
		this.statePlotter.addData(
				SERIES,
				currentTime,
				state2int(this.currentState)) ;
		if(componentRef != null) {
			try {
				this.componentRef.setEmbeddingComponentStateValue(Oven.Oven_STATE, this.currentState);
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}
	}
	
	
	
	// -------------------------------------------------------------------------
	// Model-specific methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI#disposePlotters()
	 */
	@Override
	public void			disposePlotters() throws Exception
	{
		if (this.statePlotter != null) {
			this.statePlotter.dispose() ;
			this.statePlotter = null ;
		}
	}
	
	public void activateEnergyEco() {
		this.modeEco = true;
		if(temperature.v > MAX_ECO_TEMPERATURE) {
			temperature.v = (double) MAX_ECO_TEMPERATURE;
			temperature.time = this.getCurrentStateTime();
		}
		else if(this.currentState == OvenMode.Off){
			this.last_value_temperature = MAX_ECO_TEMPERATURE;
		}
	}
	
	public void deactivateEnergyEco() {
		this.modeEco = false;
		if(temperature.v < DEFAULT_OVEN_TEMPERATURE && this.currentState == OvenMode.On) {
			temperature.v = (double) DEFAULT_OVEN_TEMPERATURE;
			temperature.time = this.getCurrentStateTime();
		}
		else {
			this.last_value_temperature = DEFAULT_OVEN_TEMPERATURE;
		}
		
	}
	
	public double getTEMPERATURE() {
		return this.temperature.v;
	}
	
	public boolean isEcoActivated() {
		return modeEco;
	}
	

}
