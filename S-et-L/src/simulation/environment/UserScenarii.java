package simulation.environment;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.OpenDoor;
import simulation.TV.events.TVSwitch;
import simulation.oven.events.OvenSwitchEvent;

public class UserScenarii {
	
	public static Set<ES_EventI> createGlobalScenario(){
		Set<ES_EventI> events = new HashSet<ES_EventI>();
		Time t = new Time(7.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(300, TimeUnit.SECONDS))));
		events.add(new TVSwitch(new Time(1.0, TimeUnit.SECONDS)));
		events.add(new TVSwitch(new Time(1000.0, TimeUnit.SECONDS)));
		t = new Time(3007.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(2000.0, TimeUnit.SECONDS))));
		events.add(new TVSwitch(new Time(2000.0, TimeUnit.SECONDS)));
		events.add(new TVSwitch(new Time(4500.0, TimeUnit.SECONDS)));
		events.add(new OvenSwitchEvent(new Time(4501.0, TimeUnit.SECONDS)));
		events.add(new OpenDoor(new Time(6000.0, TimeUnit.SECONDS)));
		return events;
	}
	
	public static Set<ES_EventI> createFridgeScenario(){
		Set<ES_EventI> events = new HashSet<ES_EventI>();
		Time t = new Time(7.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(300, TimeUnit.SECONDS))));
		t = new Time(1504.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(2000.0, TimeUnit.SECONDS))));
		t = new Time(4101.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(309.0, TimeUnit.SECONDS))));
		return events;
	}

}
