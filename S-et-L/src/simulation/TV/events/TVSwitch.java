package simulation.TV.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;


/**
 * The class <code>TVOn</code> describes the event turning on the TV 
 * @author laurie
 *
 */
public class TVSwitch 
extends Event{

	public TVSwitch(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String		eventAsString()
	{
		return "TVSwitch(" +
						this.getTimeOfOccurrence().getSimulatedTime() +" "+this.getTimeOfOccurrence().getTimeUnit()+ ")" ;
	}

}
