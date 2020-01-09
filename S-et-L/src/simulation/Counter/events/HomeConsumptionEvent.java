package simulation.Counter.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class HomeConsumptionEvent 
extends Event{
	
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	public HomeConsumptionEvent(Time timeOfOccurrence,double cons) {
		super(timeOfOccurrence, new Reading(cons));
	}

	
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	public static class Reading implements EventInformationI{
		
		private static final long serialVersionUID = 1L;
		protected final double value;
		
		public Reading(double value) {
			super();
			this.value = value;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "HomeConsumption(" + this.eventContentAsString() + ")" ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventContentAsString()
	 */
	@Override
	public String		eventContentAsString()
	{
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"level = " + ((Reading)this.getEventInformation()).value
												+ " watt";
	}

}
