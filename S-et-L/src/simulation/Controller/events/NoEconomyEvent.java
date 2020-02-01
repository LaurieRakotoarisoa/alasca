package simulation.Controller.events;

import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.tv.mil.models.TVStateMILModel;
import clean.equipments.tv.sil.TVStateSILModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class NoEconomyEvent
extends ES_Event{
	
	private static final long serialVersionUID = 1L;

	public NoEconomyEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "NoEconomyEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		if(model instanceof TVStateMILModel) {
			TVStateMILModel tv = (TVStateMILModel) model;
			tv.deactivateEnergyEco();
		}
		else if(model instanceof FridgeStateMILModel){
			FridgeStateMILModel f = (FridgeStateMILModel)model;
			f.setEcoMode(false);
		}
	}

}
