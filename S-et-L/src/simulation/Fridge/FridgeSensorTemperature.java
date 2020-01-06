package simulation.Fridge;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.Fridge.events.HighTemperatureEvent;
import simulation.Fridge.events.LowTemperatureEvent;

@ModelExternalEvents(exported = {LowTemperatureEvent.class,
								HighTemperatureEvent.class})
public class FridgeSensorTemperature 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "fridge-sensor-temperature";
	
	/** true if temperature is lower than target temperature + difference limit */
	protected boolean lowReached;
	
	/** true if temperature is higher than target temperature + difference limit */
	protected boolean highReached;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public FridgeSensorTemperature(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.setDebugLevel(1);
		this.lowReached = false;
		this.highReached = false;
	}
	
	// -------------------------------------------------------------------------
	// HIOA Model Variables
	// -------------------------------------------------------------------------
	@ImportedVariable (type =  Double.class)
	protected Value<Double> temperature;


	@Override
	public Vector<EventI> output() {
		if(lowReached) {
			Vector<EventI> ret = new Vector<EventI>();
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			LowTemperatureEvent e = new LowTemperatureEvent(t);
			ret.add(e);
			lowReached = false;
			return ret;
		}
		else if(highReached) {
			Vector<EventI> ret = new Vector<EventI>();
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			HighTemperatureEvent e = new HighTemperatureEvent(t);
			ret.add(e);
			highReached = false;
			return ret;
		}
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return new Duration(5.0,this.getSimulatedTimeUnit());
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.logMessage("internal|elapsedTime = "+elapsedTime.getSimulatedDuration());
		if(this.temperature.v > FridgeTemperature.TARGET_TEMP + FridgeTemperature.DIF_LIMIT) {
			this.highReached = true;
			assert highReached && !lowReached;
		}
		else if(this.temperature.v < FridgeTemperature.TARGET_TEMP - FridgeTemperature.DIF_LIMIT) {
			this.lowReached = true;
			assert !highReached && lowReached;
		}
		else {
			assert !(lowReached && highReached);
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		final String uri = this.getURI() ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					/**
					 * @see fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI#getModelURI()
					 */
					@Override
					public String getModelURI() { return uri ; }

					/**
					 * @see java.lang.Object#toString()
					 */
					@Override
					public String toString() { return "FridgeSensorTemperature()" ; }
		};
	}

}
