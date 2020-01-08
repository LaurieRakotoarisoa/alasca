package simulation.oven.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import utils.oven.OvenMode;

public class OvenStateEvent
extends Event{
	
	/** state of the Oven */
	private OvenMode state;

	public OvenStateEvent(Time timeOfOccurrence,OvenMode state) {
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
		return "OvenStateEvent(" +
						this.getTimeOfOccurrence().getSimulatedTime() + ", "+this.state+")" ;
	}

}
