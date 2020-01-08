package simulation2.Controller.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
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

}
