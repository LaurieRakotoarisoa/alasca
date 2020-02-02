package clean.environment.events.fridge;

import clean.equipments.fridge.sil.models.FridgeStateSILModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.Fridge.events.CloseDoor;

public class CloseDoorSIL 
extends CloseDoor{

	public CloseDoorSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeStateSILModel;
		FridgeStateSILModel f = (FridgeStateSILModel) model;
		try {
			f.getComponentRef().closeDoor();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

}
