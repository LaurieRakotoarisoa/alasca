package clean.equipments.tv.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import clean.equipments.tv.components.TV;
import clean.equipments.tv.mil.models.TVStateMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.TV.events.TvStateEvent;
import utils.TVMode;

public class TVStateSILModel extends 
AtomicHIOA
implements SGMILModelImplementationI{

	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public TVStateSILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setDebugLevel(2);
		states = new Vector<TvStateEvent>();
		assert this.tvBack != null;

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
	
	public static final String URI = TVStateSILModel.class.getName();
	
	/** stored output events for report */
	protected Vector<TvStateEvent> states;
	
	/** current state of the TV (On, Off) */
	protected TVMode currentState;
	
	public static final double	PEEK_DELAY = 0.1 ; // in seconds
	
	private static final String	SERIES = "TV state" ;
	
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
	
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected TV componentRef ;
	
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
		// The reference to the embedding component
		this.componentRef =
			(TV) simParams.get(TVStateMILModel.COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = TVMode.Off;
		this.modeEco = false;
		this.last_value_backlight = DEFAULT_TV_BACKLIGHT;

		PlotterDescription pd =
				new PlotterDescription(
						"TV State Model",
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
		
		this.tvBack.v = 0.0;
		assert	startTime.equals(this.tvBack.time) ;
		
		this.statePlotter.addData(
				SERIES,
				startTime.getSimulatedTime(),
				state2int(this.currentState)) ;
		
		super.initialiseVariables(startTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.logMessage("component state = " +
				currentState) ;
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			try {
				this.logMessage("component state = " +
								currentState) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
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
		return new Duration(PEEK_DELAY, this.getSimulatedTimeUnit()) ;
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new TVStateMILModel.TVStateModelReport(this.getURI(),states);
	}
	
	public void switchState(double currentTime) {
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
		
		this.statePlotter.addData(
				SERIES,
				currentTime,
				state2int(oldState)) ;
		this.statePlotter.addData(
				SERIES,
				currentTime,
				state2int(this.currentState)) ;
		
	}
	
	// -------------------------------------------------------------------------
	// Model-specific methods
	// -------------------------------------------------------------------------
	
	public TV getComponentRef() {
		return this.componentRef;
	}
	
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
		if(tvBack.v > MAX_ECO_BACKLIGHT) {
			tvBack.v = MAX_ECO_BACKLIGHT;
			tvBack.time = this.getCurrentStateTime();
		}
		else if(this.currentState == TVMode.Off){
			this.last_value_backlight = MAX_ECO_BACKLIGHT;
		}
	}
	
	public void deactivateEnergyEco() {
		this.modeEco = false;
		if(tvBack.v < DEFAULT_TV_BACKLIGHT && this.currentState == TVMode.On) {
			tvBack.v = DEFAULT_TV_BACKLIGHT;
			tvBack.time = this.getCurrentStateTime();
		}
		else {
			this.last_value_backlight = DEFAULT_TV_BACKLIGHT;
		}
		
	}
	
	public double getBacklight() {
		return this.tvBack.v;
	}
	
	public boolean isEcoActivated() {
		return modeEco;
	}

}
