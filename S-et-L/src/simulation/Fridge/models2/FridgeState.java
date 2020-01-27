package simulation.Fridge.models2;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.Fridge.events.ActiveCompressor;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.InactiveCompressor;
import simulation.Fridge.events.LowTemperatureEvent;
import simulation.Fridge.events.OpenDoor;
import utils.fridge.FridgeMode;

@ModelExternalEvents(exported = {ActiveCompressor.class,
								InactiveCompressor.class},
					imported = {OpenDoor.class,
								CloseDoor.class,
								EconomyEvent.class,
								NoEconomyEvent.class})
public class FridgeState 
extends AtomicModel{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------


	public FridgeState(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.targetTemperature = 4.0;
		this.currentTemperature = targetTemperature;
		this.compressorActive = true;
		this.sentEvent = false;
		this.currentRate = DEFAULT_RATE;
		this.doorOpened = false;
		this.ecoMode = false;
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "fridge-state";
	
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
	
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		
		String vname = this.getURI() + ":" +
					FRIDGE_TEMP_PLOTTING_PARAM_NAME;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.tempPlotter = new XYPlotter(pd);
		this.tempPlotter.createSeries(SERIES) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.tempPlotter != null) {
			this.tempPlotter.initialise() ;
			this.tempPlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
		if (this.tempPlotter != null) {
			this.tempPlotter.addData(
				SERIES,
				initialTime.getSimulatedTime(),
				this.targetTemperature) ;
		}
	}
		
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;
		computeTemp();
		
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
		EventI e = current.get(0);
		
		if(e instanceof OpenDoor) {
			assert !this.doorOpened;
			if(compressorActive) {
				if(this.ecoMode) this.currentRate = -LOW_RATE*0.9;
				else this.currentRate = -LOW_RATE;
			}
			else {
				this.currentRate = +HIGH_RATE;
			}
			this.doorOpened = true;
		}
		
		else if(e instanceof CloseDoor) {
			assert this.doorOpened;
			if(this.compressorActive) {
				if(this.ecoMode) this.currentRate = -LOW_RATE;
				else this.currentRate = -DEFAULT_RATE;
			}
			else this.currentRate = +DEFAULT_RATE;
			this.doorOpened = false;
		}
		
		else if(e instanceof EconomyEvent) {
			assert !this.ecoMode;
			this.ecoMode = true;
			
		}
		
		else if(e instanceof NoEconomyEvent) {
			assert this.ecoMode;
			this.ecoMode = false;
		}
		
		this.sentEvent = true;
	}

	@Override
	public Vector<EventI> output() {
		if(sentEvent) {
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
			Vector<EventI> ret = new Vector<EventI>();
			if(compressorActive) {
				ret.add(new ActiveCompressor(t,this.doorOpened,this.ecoMode));
			}
			else {
				ret.add(new InactiveCompressor(t,this.doorOpened));
			}
			this.sentEvent = false;
			return ret;
			
		}
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if(this.sentEvent) return Duration.zero(TimeUnit.SECONDS);
		return new Duration(10.0,TimeUnit.SECONDS);
	}
	
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

}
