package simulation2.oven.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class OvenSwitchEvent 
extends Event{

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

}
