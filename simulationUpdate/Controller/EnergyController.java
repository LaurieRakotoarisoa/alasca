package simulation.Controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class EnergyController 
extends CoupledModel{

	public EnergyController(String uri,
	TimeUnit simulatedTimeUnit,
	SimulatorI simulationEngine,
	ModelDescriptionI[] submodels,
	Map<Class<? extends EventI>, EventSink[]> imported,
	Map<Class<? extends EventI>, ReexportedEvent> reexported,
	Map<EventSource, EventSink[]> connections
	) throws Exception
{
	super(uri, simulatedTimeUnit, simulationEngine, submodels,
		  imported, reexported, connections);
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Energy-Controller";

}
