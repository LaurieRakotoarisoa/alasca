package simulation.Fridge.models2;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;

public class TestAlone {

	public static void main(String[] args) {
		AtomicEngine e = new AtomicEngine();
		try {
			new FridgeState(FridgeState.URI, TimeUnit.SECONDS, e);
			e.doStandAloneSimulation(0, 600);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
