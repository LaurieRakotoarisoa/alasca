package simulation.Counter;

import java.util.Vector;
import java.util.concurrent.TimeUnit;


import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.Counter.events.HomeConsumptionEvent;


@ModelExternalEvents( exported = { HomeConsumptionEvent.class} )
public class CounterModel 
extends AtomicModel{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CounterModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "counter-model";
	
	private double currentConsumption = 0.0;
	
	// -------------------------------------------------------------------------
	// Simulation methods and protocol
	// -------------------------------------------------------------------------

	@Override
	public Vector<EventI> output() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Duration timeAdvance() {
		// TODO Auto-generated method stub
		return null;
	}

}
