package simulation.models.oven.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class PowerReading 
extends Event{
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	
	/**
	 * The class <code>Reading</code> implements the power value as an
	 * event content.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * @author	Laurie Rakotoarisoa
	 */
	public static class	Reading
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		public final double	value ;

		public			Reading(double value)
		{
			super();
			this.value = value;
		}
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L ;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	/**
	 * create a new event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	timeOfOccurrence != null
	 * pre	powerReading &gt;= 0.0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence		time of occurrence of the event.
	 * @param powerReading	the value of the bandwidth in Watts.
	 */
	public PowerReading(Time timeOfOccurrence, double powerReading) {
		super(timeOfOccurrence, new Reading(powerReading));
		assert powerReading >= 0.0;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "PowerReading(" + this.eventContentAsString() + ")" ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventContentAsString()
	 */
	@Override
	public String		eventContentAsString()
	{
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"power = " + ((Reading)this.getEventInformation()).value
											+ " Watts" ;
	}
	

}
