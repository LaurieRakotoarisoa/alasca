package simulation.TV.models;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import simulation.oven.models.OvenStateModel;

public class AtomicTest {
	public static void main(String [] args) {
		AtomicEngine e = new AtomicEngine();
		try {
			Vector<Time> times = new Vector<Time>();
			times.addElement(new Time(90.0, TimeUnit.MINUTES));
			times.addElement(new Time(180.0, TimeUnit.MINUTES));
			//new TVUserModel(TVUserModel.URI,TimeUnit.MINUTES, e);
			new OvenStateModel(OvenStateModel.URI, TimeUnit.SECONDS, e);
			e.doStandAloneSimulation(0.0, 620.0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
