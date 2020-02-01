package clean.equipments.fridge.mil;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class RunFridgeMILSimulation {	
	public static void	main(String[] args)
	{
		try {
			Architecture localArchitecture =
								FridgeMILCoupledModel.buildArchitecture();
			SimulationEngine se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 7000.0) ;
			Thread.sleep(5000L) ;
			SGMILModelImplementationI m =
					(SGMILModelImplementationI)
										se.getDescendentModel(se.getURI()) ;
			m.disposePlotters() ;
			System.out.println("end.") ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
