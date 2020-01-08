package simulation2.oven.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>OvenConsumptionEvent</code> describes an event
 * giving the information of the consumption of the Oven at a time
 * 
 * @author Saad CHIADMI
 *
 */
public class OvenConsumptionEvent
extends Event{

	public OvenConsumptionEvent(Time timeOfOccurrence, double consumption) {
		super(timeOfOccurrence, new Reading(consumption));
		assert	timeOfOccurrence != null && consumption >= 0.0 ;
	}
	
	public static class Reading implements EventInformationI{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected final double value;
		public Reading(double value) {
			super();
			this.value = value;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "Oven Consumption(" + this.eventContentAsString() + ")" ;
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
