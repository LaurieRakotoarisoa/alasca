package simulation.Fridge.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class CloseDoor 
extends ES_Event{

	public CloseDoor(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String		eventAsString()
	{
		return "CloseDoor(" +
						this.getTimeOfOccurrence().getSimulatedTime() +" "+this.getTimeOfOccurrence().getTimeUnit()+ ")" ;
	}

}
