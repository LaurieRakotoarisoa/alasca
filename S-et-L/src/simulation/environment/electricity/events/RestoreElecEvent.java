package simulation.environment.electricity.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class RestoreElecEvent 
extends ES_Event{

	public RestoreElecEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence,null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
