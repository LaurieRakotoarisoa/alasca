package simulation2.Controller.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
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

}
