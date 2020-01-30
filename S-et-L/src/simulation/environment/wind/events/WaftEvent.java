package simulation.environment.wind.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class WaftEvent 
extends ES_Event{

	public WaftEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "WaftEvent(" + this.eventContentAsString() + ")" ;
	}

}
