package simulation.environment.wind.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class ModerateWindEvent 
extends ES_Event{

	public ModerateWindEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
		// TODO Auto-generated constructor stub
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
		return "ModerateWindEvent(" + this.eventContentAsString() + ")" ;
	}

}
