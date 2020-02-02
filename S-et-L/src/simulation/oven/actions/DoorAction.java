package simulation.oven.actions;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/**
 * The class <code>DoorAction</code> describes the action of opening the oven door at 
 * some time and for a specified duration
 * @author Saad CHIADMI
 *
 */
public class DoorAction {
	
	private Time beginAt;
	private Duration duration;
	
	public DoorAction(double beginAt, double dur, TimeUnit tu) {
		this.beginAt = new Time(beginAt,tu);
		this.duration = new Duration(dur, tu);
	}
	
	public Time getBeginning() {
		return beginAt;
	}
	
	public Duration getDuration() {
		return duration;
	}

}
