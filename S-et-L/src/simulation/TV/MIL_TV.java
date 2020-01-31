package simulation.TV;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.TV.models.TVMILCoupledModel;

public class MIL_TV {
	
	public static void main(String [] args) {
		SimulationEngine se;
		
		try {
			Architecture localArchitecture = TVMILCoupledModel.build();
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 7000) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
	}

}
