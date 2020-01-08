package simulation.cyphy.components;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.Controller.HomeController;
import simulation.environment.electricity.Electricity_ESModel;

public class TestComponents {
	
	public static void main(String [] args) {
		testElectricity();
		
	}
	
	public static void testHomeController() {
		SimulationEngine se ;

		try {
			Architecture localArchitecture = HomeController.build() ;
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 500.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
	public static void testElectricity() {
		SimulationEngine se ;

		try {
			Architecture localArchitecture = Electricity_ESModel.build() ;
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 500.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
