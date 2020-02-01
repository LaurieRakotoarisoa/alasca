package clean.simulations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import clean.environment.UserMILModel;
import clean.equipments.controller.mil.models.ControllerMILModel;
import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import clean.equipments.tv.mil.models.TVMILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.OpenDoor;
import simulation.TV.events.TVSwitch;

public class RunMILSimulation {
	
	public static void	main(String[] args)
	{
		
		try {
			
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
					new HashMap<>() ;
			
			atomicModelDescriptors.put(
					UserMILModel.URI,
					AtomicModelDescriptor.create(
							UserMILModel.class,
							UserMILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			
			atomicModelDescriptors.put(
					ControllerMILModel.URI,
					AtomicModelDescriptor.create(
							ControllerMILModel.class,
							ControllerMILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			
			atomicModelDescriptors.putAll(FridgeMILCoupledModel.createAtomicModelDescriptors());
			
			atomicModelDescriptors.putAll(TVMILCoupledModel.createAtomicModelDescriptors());
						
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
					new HashMap<>() ;
			
			coupledModelDescriptors.putAll(FridgeMILCoupledModel.createCoupledModelDescriptors());
			coupledModelDescriptors.putAll(TVMILCoupledModel.createCoupledModelDescriptors());
			
			
			Set<String> submodels = new HashSet<String>() ;
			submodels.add(UserMILModel.URI);
			submodels.add(ControllerMILModel.URI);
			submodels.add(TVMILCoupledModel.URI);
			submodels.add(FridgeMILCoupledModel.URI);
			
			Map<EventSource,EventSink[]> connections =
					new HashMap<EventSource,EventSink[]>() ;
					
			connections.put(
			new EventSource(UserMILModel.URI,
						TVSwitch.class),
			new EventSink[] {
				new EventSink(TVMILCoupledModel.URI,
							  TVSwitch.class)
			}) ;
			connections.put(
				new EventSource(UserMILModel.URI,
						OpenDoor.class),
			new EventSink[] {
				new EventSink(FridgeMILCoupledModel.URI,
							  OpenDoor.class)
			}) ;
			connections.put(
				new EventSource(UserMILModel.URI,
						CloseDoor.class),
			new EventSink[] {
				new EventSink(FridgeMILCoupledModel.URI,
							  CloseDoor.class)
			}) ;
			connections.put(
			new EventSource(ControllerMILModel.URI, EconomyEvent.class),
			new EventSink[] {
				new EventSink(FridgeMILCoupledModel.URI,
							  EconomyEvent.class),
				new EventSink(TVMILCoupledModel.URI,
						  EconomyEvent.class)	
			}) ;
			connections.put(
			new EventSource(ControllerMILModel.URI, NoEconomyEvent.class),
			new EventSink[] {
				new EventSink(FridgeMILCoupledModel.URI,
							  NoEconomyEvent.class),
				new EventSink(TVMILCoupledModel.URI,
						  NoEconomyEvent.class)	
			}) ;
			
			coupledModelDescriptors.put(
					EnergyController.URI,
					new CoupledModelDescriptor(
							EnergyController.class,
							EnergyController.URI,
							submodels,
							null,
							null,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE)) ;
			
			ArchitectureI architecture =
					new Architecture(
							EnergyController.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;
			
			SimulationEngine se = architecture.constructSimulator() ;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
