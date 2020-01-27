package simulation.Fridge.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import utils.events.ConsumptionEventI;

/**
 * The class <code>FridgeConsumptionEvent</code> describes an event
 * giving the information of the consumption of the Fridge at a time
 * 
 * @author Saad CHIADMI
 *
 */
public class FridgeConsumptionEvent
extends Event implements ConsumptionEventI{

	public FridgeConsumptionEvent(Time timeOfOccurrence, double consumption) {
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
		return "Fridge Consumption(" + this.eventContentAsString() + ")" ;
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
	
	public double getConsumption() {
		return ((Reading)this.getEventInformation()).value;
	}
}
