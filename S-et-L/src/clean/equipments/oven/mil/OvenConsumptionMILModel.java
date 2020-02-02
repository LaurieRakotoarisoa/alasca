package clean.equipments.oven.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import components.device.Oven;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.oven.events.ActiveCompressor;
import simulation.oven.events.OvenConsumptionEvent;
import simulation.oven.events.InactiveCompressor;

@ModelExternalEvents(imported = {InactiveCompressor.class,
								ActiveCompressor.class},
					exported = { OvenConsumptionEvent.class})
public class OvenConsumptionMILModel 
extends AtomicModel
implements SGMILModelImplementationI{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------


	public OvenConsumptionMILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.consumption = DEFAULT_CONS;
		this.triggerUpdate = false;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void		finalize() throws Throwable
	{
		if (this.consPlotter != null) {
			this.consPlotter.dispose() ;
		}
		super.finalize();
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = OvenConsumptionMILModel.class.getName();
	public static final String		COMPONENT_HOLDER_REF_PARAM_NAME =
			"oven consumption component reference" ;
	
	public static final double DEFAULT_CONS = 250.0;
	public static final double LIGHT_CONS = 100.0;
	public static final double HIGH_CONS = 500.0;
	private double consumption;
	
	/** run parameter to plot the evolution of temperature */
	public static final String Oven_CONS_PLOTTING_PARAM_NAME = "oven-temp-plot";
	
	/** Frame used to plot the temperature during the simulation.			*/
	protected XYPlotter			consPlotter ;
	
	private static final String	SERIES = "oven consumption" ;
	
	private boolean triggerUpdate;
	
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentAccessI componentRef ;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

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
		
		PlotterDescription pd = new PlotterDescription(
				"Oven Consumption Model",
				"Time (sec)",
				"Consumption (watts)",
				100,
				0,
				600,
				400) ;
		this.consPlotter = new XYPlotter(pd);
		this.consPlotter.createSeries(SERIES) ;
		this.consPlotter.initialise() ;
		this.consPlotter.showPlotter() ;
		
		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
		super.initialiseState(initialTime);
		
		this.consPlotter.addData(
				SERIES,
				initialTime.getSimulatedTime(),
				this.consumption) ;
	}
	
	@Override
	public ArrayList<EventI> output() {
		if(this.triggerUpdate) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
			try {
				ret.add(new OvenConsumptionEvent(t, consumption));
			}catch (Exception e) {
				throw new RuntimeException();
			}
			this.triggerUpdate = false;
			return ret;
		}	
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if(this.componentRef == null) {
			if(this.triggerUpdate) return Duration.zero(this.getSimulatedTimeUnit());
			else return Duration.INFINITY;
		}
		else {
			return new Duration(10.0, TimeUnit.SECONDS);
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
		
		this.triggerUpdate = true;
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.consPlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.consumption) ;

		super.endSimulation(endTime) ;
	}
	
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		final String uri = this.uri ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;
					@Override
					public String getModelURI() {
						return uri ;
					}				
				};
	}
	
	// -------------------------------------------------------------------------
	// Model-specific methods
	// -------------------------------------------------------------------------
	
	public void updateConsumption(boolean compressorActivated, boolean doorOpened, boolean ecoMode) {
		if(compressorActivated) {
			if(doorOpened) {
				if(ecoMode) 
				{ this.consumption = DEFAULT_CONS;}
				
				else 
				{ this.consumption = HIGH_CONS; }
			}
			else {
				if(ecoMode) this.consumption = DEFAULT_CONS*0.9;
				else this.consumption = DEFAULT_CONS;
			}
		}
		else {
			if(doorOpened) {
				this.consumption = LIGHT_CONS;
			}
			else {
				this.consumption = 0.0;
			}
		}		
		this.consPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.consumption) ;
		
		try {
			componentRef.setEmbeddingComponentStateValue(Oven.Oven_CONS, this.consumption);
			this.logMessage("close door at "+getCurrentStateTime());
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

}
