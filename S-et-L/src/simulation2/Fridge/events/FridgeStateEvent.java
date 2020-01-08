package simulation2.Fridge.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import utils.fridge.FridgeMode;

public class FridgeStateEvent
extends Event{
	
	/** state of the Oven */
	private FridgeMode state;

	public FridgeStateEvent(Time timeOfOccurrence,FridgeMode state) {
		super(timeOfOccurrence, null);
		this.state = state;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "Fridge StateEvent(" +
						this.getTimeOfOccurrence().getSimulatedTime() + ", "+this.state+")" ;
	}

}
