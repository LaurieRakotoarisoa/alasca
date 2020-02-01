package clean.equipments.fridge.sil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class FridgeSILCoupledModel 
extends FridgeMILCoupledModel{

	
	public FridgeSILCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = FridgeSILCoupledModel.class.getName();

}
