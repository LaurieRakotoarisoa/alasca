package simulation;

import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.TV.models.TVModel;
import simulation.oven.models.OvenModel;

public class SimulationMain {
	
	public static void main (String [] args) throws Exception {
		
		SimulationEngine ovenSimulation = OvenModel.getArchitecture().constructSimulator() ;
		ovenSimulation.setDebugLevel(0);
		ovenSimulation.setSimulationRunParameters(OvenModel.getSettingRunParameters()) ;
		
		SimulationEngine tvSimulation = TVModel.build().constructSimulator() ;
		tvSimulation.setDebugLevel(0);
		tvSimulation.setSimulationRunParameters(TVModel.getSettingRunParameters()) ;
		
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
		
		long startOvenSimulation = System.currentTimeMillis() ;
		ovenSimulation.doStandAloneSimulation(0.0, 5000.0) ;
		long endOvenSimulation = System.currentTimeMillis() ;
		
		long startTVSimulation = System.currentTimeMillis() ;
		tvSimulation.doStandAloneSimulation(0.0, 5000.0) ;
		long endTVSimulation = System.currentTimeMillis() ;
		
		System.out.println(ovenSimulation.getFinalReport()) ;
		System.out.println("Oven Simulation ends. " + (endOvenSimulation - startOvenSimulation)) ;
		
		System.out.println(tvSimulation.getFinalReport()) ;
		System.out.println("TV Simulation ends. " + (endTVSimulation - startTVSimulation)) ;
		
		Thread.sleep(1000000L);
		System.exit(0) ;
		
	}

}
