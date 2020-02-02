package simulation.oven.events;

import clean.equipments.oven.mil.OvenStateMILModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class OvenSwitchEvent 
extends ES_Event{

	public OvenSwitchEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String		eventAsString()
	{
		return "Oven Switch(" +
						this.getTimeOfOccurrence().getSimulatedTime() +" "+this.getTimeOfOccurrence().getTimeUnit()+ ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		assert model instanceof OvenStateMILModel;
		OvenStateMILModel oven = (OvenStateMILModel) model;
		oven.switchState(timeOfOccurrence.getSimulatedTime());	
	}

}
