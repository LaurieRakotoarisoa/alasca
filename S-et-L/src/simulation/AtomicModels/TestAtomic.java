package simulation.AtomicModels;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;

public class TestAtomic {

	public static void main(String[] args) {
		AtomicEngine e = new AtomicEngine();
		try {
			new TVModel("TV", TimeUnit.SECONDS,e);
			e.doStandAloneSimulation(0.0, 620.0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
