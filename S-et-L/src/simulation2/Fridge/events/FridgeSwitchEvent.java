package simulation2.Fridge.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class FridgeSwitchEvent 
extends Event{

	public FridgeSwitchEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String		eventAsString()
	{
		return "Fridge Switch(" +
						this.getTimeOfOccurrence().getSimulatedTime() +" "+this.getTimeOfOccurrence().getTimeUnit()+ ")" ;
	}

}
