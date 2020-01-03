package simulation.TV;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.AtomicModels.events.TvStateEvent;
import simulation.TV.events.TVStateReading;
import simulation.TV.events.TVSwitch;
import simulation.temp.ElectricityEvent;
import simulation.temp.ElectricityModel;
import simulation.temp.Electricy_ESModel;

public class TestTV {

	public static void main(String[] args) {
		
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		try {
			atomicModelDescriptors.put(TVUserModel.URI,
					AtomicModelDescriptor.create(TVUserModel.class,
							TVUserModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TVStateModel.URI,
					AtomicModelDescriptor.create(TVStateModel.class,
							TVStateModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels1 = new HashSet<String>() ;
			submodels1.add(TVUserModel.URI);
			submodels1.add(TVStateModel.URI);
			
			Map<Class<? extends EventI>,EventSink[]> imported1 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
					
			Map<Class<? extends EventI>,ReexportedEvent> reexported1 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported1.put(
					TvStateEvent.class,
					new ReexportedEvent(TVStateModel.URI,
										TvStateEvent.class)) ;
			
			Map<EventSource,EventSink[]> connections1 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from13 =
					new EventSource(TVUserModel.URI,
									TVSwitch.class) ;
			EventSink[] to13 =
					new EventSink[] {
						new EventSink(TVStateModel.URI,
									  TVSwitch.class)} ;
			connections1.put(from13, to13) ;
			
			Map<VariableSource,VariableSink[]> bindings1 =
					new HashMap<VariableSource,VariableSink[]>() ;
			
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
					new HashMap<>() ;
			
			coupledModelDescriptors.put(
					TVModel.URI,
					new CoupledHIOA_Descriptor(
							TVModel.class,
							TVModel.URI,
							submodels1,
							imported1,
							reexported1,
							connections1,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE,
							null,
							null,
							bindings1)) ;
			
			ArchitectureI architecture =
					new Architecture(
							TVModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;
			
			SimulationEngine se = architecture.constructSimulator() ;
			se.setDebugLevel(0);
			
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			Vector<Time> eventsTime = new Vector<Time>();
			eventsTime.addElement(new Time(90.0, TimeUnit.SECONDS));
			eventsTime.addElement(new Time(180.0, TimeUnit.SECONDS));
			simParams.put(TVUserModel.URI + ":" + TVUserModel.USER_EVENTS , eventsTime);
			se.setSimulationRunParameters(simParams) ;
			
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			long start = System.currentTimeMillis() ;
			se.doStandAloneSimulation(0.0, 5000.0) ;
			long end = System.currentTimeMillis() ;
			System.out.println(se.getFinalReport()) ;
			System.out.println("Simulation ends. " + (end - start)) ;
			Thread.sleep(1000000L);
			System.exit(0) ;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
