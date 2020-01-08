package simulation2.TV.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import utils.TVMode;

public class TvStateEvent 
extends Event{
	
	/** state of the TV */
	private TVMode state;

	public TvStateEvent(Time timeOfOccurrence,TVMode state) {
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
		return "TvStateEvent(" +
						this.getTimeOfOccurrence().getSimulatedTime() + ", "+this.state+")" ;
	}

}
