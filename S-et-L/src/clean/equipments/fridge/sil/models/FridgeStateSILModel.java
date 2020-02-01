package clean.equipments.fridge.sil.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import clean.equipments.fridge.components.FridgeComponent;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.Fridge.events.ActiveCompressor;
import simulation.Fridge.events.InactiveCompressor;

public class FridgeStateSILModel 
extends AtomicModel
implements SGMILModelImplementationI{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------


	public FridgeStateSILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setDebugLevel(2);
		this.targetTemperature = 4.0;
		this.currentTemperature = targetTemperature;
		this.compressorActive = true;
		this.sentEvent = false;
		this.currentRate = DEFAULT_RATE;
		this.doorOpened = false;
		this.ecoMode = false;
		
		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void		finalize() throws Throwable
	{
		if (this.tempPlotter != null) {
			this.tempPlotter.dispose() ;
		}
		super.finalize();
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = FridgeStateSILModel.class.getName();
	
	public static double DEFAULT_RATE = 0.05;
	public static double HIGH_RATE = 0.2;
	public static double LOW_RATE = 0.01;
	
	public static final double MAX_DIFF_TEMP = 2.0;
	private double targetTemperature;
	private double currentTemperature;
	private boolean compressorActive;
	private double currentRate;
	private boolean doorOpened;
	
	private boolean sentEvent;
	
	private boolean ecoMode;
	

	
	/** run parameter to plot the evolution of temperature */
	public static final String FRIDGE_TEMP_PLOTTING_PARAM_NAME = "fridge-temp-plot";
	
	/** Frame used to plot the temperature during the simulation.			*/
	protected XYPlotter			tempPlotter ;
	
	private static final String	SERIES = "Fridge temperature" ;
	
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected FridgeComponent componentRef ;
	
	
	// -------------------------------------------------------------------------
	// HIOA model variables
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
			(FridgeComponent)
							simParams.get(FridgeStateMILModel.COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		PlotterDescription pd =
				new PlotterDescription(
						"Fridge Temperature Model",
						"Time (sec)",
						"Temperature (C°)",
						100,
						0,
						600,
						400) ;
		
		this.tempPlotter = new XYPlotter(pd);
		this.tempPlotter.createSeries(SERIES) ;
		
		this.tempPlotter.initialise() ;
		this.tempPlotter.showPlotter() ;
		
		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
		super.initialiseState(initialTime);
		
		this.tempPlotter.addData(
				SERIES,
				initialTime.getSimulatedTime(),
				this.targetTemperature) ;
	}
		
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;
		//Mode eco Door has changed ?
		assert this.componentRef != null;
		computeTemp();
		
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
		
		this.sentEvent = true;
	}

	@Override
	public ArrayList<EventI> output() {
		if(sentEvent) {
			this.logMessage("emitting new compressor state event") ;
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
			ArrayList<EventI> ret = new ArrayList<EventI>();
			if(compressorActive) {				
				try {
					ret.add(new ActiveCompressor(t,this.doorOpened,this.ecoMode));
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}
			else {
				try {
					ret.add(new InactiveCompressor(t,this.doorOpened));
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
				
			}
			this.sentEvent = false;
			return ret;
			
		}
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if(this.componentRef == null) {
			if(this.sentEvent) return Duration.zero(this.getSimulatedTimeUnit());
			else return new Duration(10.0,TimeUnit.SECONDS);
		}
		return new Duration(10.0,TimeUnit.SECONDS);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.tempPlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.currentTemperature) ;

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
	
	private void computeTemp(){
		
		if (this.tempPlotter != null) {
			this.tempPlotter.addData(SERIES,
							 				this.getCurrentStateTime().getSimulatedTime(),
							 				this.currentTemperature) ;
		}
		
		this.currentTemperature += this.currentRate;
		
		
		
		updateStateCompressor();
		
	}
	/**
	 * change compressor state if a limit has been reached
	 */
	private void updateStateCompressor() {
		boolean lastStateCompressor = this.compressorActive;
		if(this.currentTemperature>= targetTemperature + MAX_DIFF_TEMP) {
			this.compressorActive = true;
			if(this.doorOpened) {
				if(this.ecoMode) this.currentRate = -LOW_RATE*0.9;
				else this.currentRate = -LOW_RATE;
			}
			else {
				if(this.ecoMode) this.currentRate = -LOW_RATE;
				else this.currentRate = -DEFAULT_RATE;
			}
		}
		else if(this.currentTemperature <= targetTemperature - MAX_DIFF_TEMP) {
			this.compressorActive = false;
			if(doorOpened) this.currentRate = +HIGH_RATE;
			else this.currentRate = +DEFAULT_RATE;
		}
		if(lastStateCompressor != this.compressorActive) {
			this.sentEvent = true;
		}
	}
	
	public void openDoor() {
		assert !this.doorOpened;
		if(compressorActive) {
			if(this.ecoMode) this.currentRate = -LOW_RATE*0.9;
			else this.currentRate = -LOW_RATE;
		}
		else {
			this.currentRate = +HIGH_RATE;
		}
		this.doorOpened = true;
		this.logMessage("open door at "+getCurrentStateTime());
	}
	
	public void closeDoor() {
		assert this.doorOpened;
		if(this.compressorActive) {
			if(this.ecoMode) this.currentRate = -LOW_RATE;
			else this.currentRate = -DEFAULT_RATE;
		}
		else this.currentRate = +DEFAULT_RATE;
		this.doorOpened = false;
		this.logMessage("close door at "+getCurrentStateTime());
	}
	
	public void setEcoMode(boolean mode) {
		assert this.ecoMode != mode;
		this.ecoMode = mode;
		this.logMessage("Economy mode "+this.ecoMode+" at "+this.getCurrentStateTime());
	}

}
