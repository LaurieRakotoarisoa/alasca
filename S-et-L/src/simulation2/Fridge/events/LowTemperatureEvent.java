package simulation2.Fridge.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class LowTemperatureEvent
extends Event{

	public LowTemperatureEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
