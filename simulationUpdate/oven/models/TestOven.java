package simulation.oven.models;

import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class TestOven {
	
	public static void main(String[] args) throws Exception {
		
		
			SimulationEngine se = OvenModel.getArchitecture().constructSimulator() ;
			se.setDebugLevel(0);
			
			se.setSimulationRunParameters(OvenModel.getSettingRunParameters()) ;
			
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			long start = System.currentTimeMillis() ;
			se.doStandAloneSimulation(0.0, 5000.0) ;
			long end = System.currentTimeMillis() ;
			System.out.println(se.getFinalReport()) ;
			System.out.println("Simulation ends. " + (end - start)) ;
			Thread.sleep(1000000L);
			System.exit(0) ;
			
	}

}
