package simulation.Fridge.events;

import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class InactiveCompressor 
extends Event{
	
	private static class Reading
	implements EventInformationI{
		
		private static final long serialVersionUID = 1L;
		public final boolean doorOpened;
		
		private Reading(boolean doorOpened) {
			super();
			this.doorOpened = doorOpened;
		}
	}

	public InactiveCompressor(Time timeOfOccurrence,boolean doorOpened) {
		super(timeOfOccurrence, new Reading(doorOpened));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean isDoorOpened() {
		return ((Reading) content).doorOpened;
	}
	
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeConsumptionMILModel;
		FridgeConsumptionMILModel f = (FridgeConsumptionMILModel) model;
		//eco mode set false as we don't use it
		f.updateConsumption(false, isDoorOpened(),false);
	}

}
