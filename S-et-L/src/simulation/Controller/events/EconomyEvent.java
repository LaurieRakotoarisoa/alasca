package simulation.Controller.events;

import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.tv.mil.models.TVStateMILModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class EconomyEvent 
extends ES_Event{

	public EconomyEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "EconomyEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		if(model instanceof TVStateMILModel) {
			TVStateMILModel tv = (TVStateMILModel) model;
			if(!tv.isEcoActivated()) tv.activateEnergyEco();
			assert tv.isEcoActivated();
		}
		else if(model instanceof FridgeStateMILModel){
			FridgeStateMILModel f = (FridgeStateMILModel)model;
			f.setEcoMode(true);
		}
	}

}
