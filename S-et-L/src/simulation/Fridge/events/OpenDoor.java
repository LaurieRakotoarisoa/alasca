package simulation.Fridge.events;

import clean.equipments.fridge.mil.FridgeStateMILModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class OpenDoor 
extends ES_Event{

	public OpenDoor(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String		eventAsString()
	{
		return "OpenDoor(" +
						this.getTimeOfOccurrence().getSimulatedTime() +" "+this.getTimeOfOccurrence().getTimeUnit()+ ")" ;
	}
	
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeStateMILModel;
		FridgeStateMILModel f = (FridgeStateMILModel) model;
		f.openDoor();
	}
}
