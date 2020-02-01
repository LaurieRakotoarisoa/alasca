package simulation.Fridge.events;

import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class ActiveCompressor
extends Event{
	
	private boolean ecoModeActivated;
	
	private static class Reading
	implements EventInformationI{
		
		private static final long serialVersionUID = 1L;
		public final boolean doorOpened;
		
		private Reading(boolean doorOpened) {
			super();
			this.doorOpened = doorOpened;
		}
	}

	public ActiveCompressor(Time timeOfOccurrence, boolean doorOpened, boolean ecoMode) {
		super(timeOfOccurrence, new Reading(doorOpened));
		this.ecoModeActivated = ecoMode;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean isDoorOpened() {
		return ((Reading) content).doorOpened;
	}
	
	public boolean isEcoModeActivated() {
		return ecoModeActivated;
	}
	
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeConsumptionMILModel;
		FridgeConsumptionMILModel f = (FridgeConsumptionMILModel) model;
		f.updateConsumption(true, isDoorOpened(), ecoModeActivated);
	}

}
