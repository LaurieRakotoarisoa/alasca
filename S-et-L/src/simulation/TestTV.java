package simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.examples.molene.MoleneModel;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
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
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.Controller.EnergyController;
import simulation.Controller.HomeController;
import simulation.Controller.TVController;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.Fridge.actions.DoorAction;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.OpenDoor;
import simulation.Fridge.models.FridgeV2Model;
import simulation.Fridge.models.UserFridgeModel;
import simulation.TV.events.TVSwitch;
import simulation.TV.models.TVConsumption;
import simulation.TV.models.TVModel;
import simulation.TV.models.TVStateModel;
import simulation.TV.models.TVUserModel;
import simulation.environment.UserModel;
import simulation.environment.electricity.Electricity_ESModel;
import simulation.environment.electricity.events.RestoreElecEvent;
import simulation.environment.electricity.events.SheddingEvent;
public class TestTV {

	public static void main(String[] args) {
		
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		try {
			
				
			// ----------------------------------------------------------------
			// TV Model 
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(TVUserModel.URI,
					AtomicModelDescriptor.create(TVUserModel.class,
							TVUserModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TVStateModel.URI,
					AtomicModelDescriptor.create(TVStateModel.class,
							TVStateModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TicModel.URI+"-1",
					AtomicModelDescriptor.create(TicModel.class,
							TicModel.URI+"-1",
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TVConsumption.URI,
					AtomicModelDescriptor.create(TVConsumption.class,
							TVConsumption.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels1 = new HashSet<String>() ;
			//submodels1.add(TVUserModel.URI);
			submodels1.add(TVStateModel.URI);
			submodels1.add(TicModel.URI+"-1");
			submodels1.add(TVConsumption.URI);
			
			Map<Class<? extends EventI>,EventSink[]> imported1 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
					
			imported1.put(EconomyEvent.class,
					new EventSink[] {
							new EventSink(TVStateModel.URI,
									EconomyEvent.class)});
			
			imported1.put(NoEconomyEvent.class,
					new EventSink[] {
							new EventSink(TVStateModel.URI,
									NoEconomyEvent.class)});
			
			imported1.put(TVSwitch.class,
					new EventSink[] {
							new EventSink(TVStateModel.URI,
									TVSwitch.class)});
					
			Map<Class<? extends EventI>,ReexportedEvent> reexported1 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			
			Map<EventSource,EventSink[]> connections1 =
					new HashMap<EventSource,EventSink[]>() ;
			
			EventSource from12 =
					new EventSource(TicModel.URI+"-1",
									TicEvent.class) ;
			EventSink[] to12 =
					new EventSink[] {
						new EventSink(TVConsumption.URI,
									  TicEvent.class)} ;
			connections1.put(from12, to12) ;
			
			Map<VariableSource,VariableSink[]> bindings1 =
					new HashMap<VariableSource,VariableSink[]>() ;
					
			VariableSource source11 =
					new VariableSource("tvBack",
									   Double.class,
									   TVStateModel.URI) ;
				VariableSink[] sinks11 =
					new VariableSink[] {
							new VariableSink("tvBack",
											 Double.class,
											 TVConsumption.URI)} ;
					
			bindings1.put(source11, sinks11);
			
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
			
			// ----------------------------------------------------------------
			// TV Controller
			// ----------------------------------------------------------------
//			atomicModelDescriptors.put(TVController.URI,
//			AtomicModelDescriptor.create(TVController.class,
//					TVController.URI,
//					TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Fridge Model
			// ----------------------------------------------------------------

			
//			atomicModelDescriptors.put(FridgeTemperature.URI,
//			AtomicModelDescriptor.create(FridgeTemperature.class,
//					FridgeTemperature.URI,
//					TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
//			
//			atomicModelDescriptors.put(FridgeSensorTemperature.URI,
//					AtomicModelDescriptor.create(FridgeSensorTemperature.class,
//							FridgeSensorTemperature.URI,
//							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
//
//			Set<String> submodels2 = new HashSet<String>() ;
//			submodels2.add(FridgeTemperature.URI);
//			submodels2.add(FridgeSensorTemperature.URI);
//			
//			Map<EventSource,EventSink[]> connections2 =
//					new HashMap<EventSource,EventSink[]>() ;
//					
//			Map<VariableSource,VariableSink[]> bindings2 =
//					new HashMap<VariableSource,VariableSink[]>() ;
//					
//			VariableSource source21 =
//					new VariableSource("temperature",
//									   Double.class,
//									   FridgeTemperature.URI) ;
//				VariableSink[] sinks21 =
//					new VariableSink[] {
//							new VariableSink("temperature",
//											 Double.class,
//											 FridgeSensorTemperature.URI)} ;
//					
//			bindings2.put(source21, sinks21);
			
//			atomicModelDescriptors.put(UserFridgeModel.URI,
//			AtomicModelDescriptor.create(UserFridgeModel.class,
//					UserFridgeModel.URI,
//					TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(FridgeV2Model.URI,
					AtomicModelDescriptor.create(FridgeV2Model.class,
							FridgeV2Model.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));

			Set<String> submodels2 = new HashSet<String>() ;
			submodels2.add(UserFridgeModel.URI);
			submodels2.add(FridgeV2Model.URI);
			
			Map<Class<? extends EventI>,EventSink[]> imported2 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
					
			imported2.put(EconomyEvent.class,
					new EventSink[] {
							new EventSink(FridgeV2Model.URI,
									EconomyEvent.class)});
			imported2.put(NoEconomyEvent.class,
					new EventSink[] {
							new EventSink(FridgeV2Model.URI,
									NoEconomyEvent.class)});
			
			Map<EventSource,EventSink[]> connections2 =
					new HashMap<EventSource,EventSink[]>() ;
					
					
					
			Map<VariableSource,VariableSink[]> bindings2 =
					new HashMap<VariableSource,VariableSink[]>() ;
			
//			coupledModelDescriptors.put(
//					FridgeModel.URI,
//					new CoupledHIOA_Descriptor(
//							FridgeModel.class,
//							FridgeModel.URI,
//							submodels2,
//							imported2,
//							null,
//							connections2,
//							null,
//							SimulationEngineCreationMode.COORDINATION_ENGINE,
//							null, null, bindings2)) ;
			
			// ----------------------------------------------------------------
			// Home Controller model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(HomeController.URI,
					AtomicModelDescriptor.create(HomeController.class,
							HomeController.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));

			
			// ----------------------------------------------------------------
			// Electricity model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(Electricity_ESModel.URI,
					AtomicModelDescriptor.create(Electricity_ESModel.class,
							Electricity_ESModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// User model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(UserModel.URI,
					AtomicModelDescriptor.create(UserModel.class,
							UserModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Full architecture and Global model
			// ----------------------------------------------------------------
			Set<String> submodels3 = new HashSet<String>() ;
			submodels3.add(TVModel.URI);
			submodels3.add(FridgeV2Model.URI);
			submodels3.add(Electricity_ESModel.URI);
			submodels3.add(HomeController.URI);
			submodels3.add(UserModel.URI);
			
			Map<EventSource,EventSink[]> connections3 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from31 =
					new EventSource(
							HomeController.URI,
							EconomyEvent.class) ;
			EventSink[] to31 =
					new EventSink[] {
							new EventSink(
									TVModel.URI,
									EconomyEvent.class),
							new EventSink(
									FridgeV2Model.URI,
									EconomyEvent.class)} ;
			connections3.put(from31, to31) ;
			
			EventSource from32 =
					new EventSource(
							HomeController.URI,
							NoEconomyEvent.class) ;
			EventSink[] to32 =
					new EventSink[] {
							new EventSink(
									TVModel.URI,
									NoEconomyEvent.class),
							new EventSink(
									FridgeV2Model.URI,
									NoEconomyEvent.class)} ;
			connections3.put(from32, to32) ;
			
			EventSource from33 =
					new EventSource(
							Electricity_ESModel.URI,
							SheddingEvent.class) ;
			EventSink[] to33 =
					new EventSink[] {
							new EventSink(
									HomeController.URI,
									SheddingEvent.class)} ;
			connections3.put(from33, to33) ;
			
			EventSource from34 =
					new EventSource(
							Electricity_ESModel.URI,
							RestoreElecEvent.class) ;
			EventSink[] to34 =
					new EventSink[] {
							new EventSink(
									HomeController.URI,
									RestoreElecEvent.class)} ;
			connections3.put(from34, to34) ;
			
			EventSource from35 =
					new EventSource(
							UserModel.URI,
							TVSwitch.class) ;
			
			EventSink[] to35 =
					new EventSink[] {
							new EventSink(
									TVModel.URI,
									TVSwitch.class)} ;
			connections3.put(from35, to35) ;
			
			EventSource from36 =
					new EventSource(UserModel.URI,
									OpenDoor.class) ;
			EventSink[] to36 =
					new EventSink[] {
						new EventSink(FridgeV2Model.URI,
									  OpenDoor.class)} ;
			connections3.put(from36, to36) ;
			
			EventSource from37 =
					new EventSource(UserModel.URI,
									CloseDoor.class) ;
			EventSink[] to37 =
					new EventSink[] {
						new EventSink(FridgeV2Model.URI,
									  CloseDoor.class)} ;
			connections3.put(from37, to37) ;
			
			coupledModelDescriptors.put(
					EnergyController.URI,
					new CoupledModelDescriptor(
							EnergyController.class,
							EnergyController.URI,
							submodels3,
							null,
							null,
							connections3,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE)) ;

	
			
			ArchitectureI architecture =
					new Architecture(
							EnergyController.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;
			
			SimulationEngine se = architecture.constructSimulator() ;
			se.setDebugLevel(0);
			
			
			//Setting run parameters
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			//TV User Model
			Vector<Time> eventsTime = new Vector<Time>();
			eventsTime.addElement(new Time(90.0, TimeUnit.SECONDS));
			eventsTime.addElement(new Time(180.0, TimeUnit.SECONDS));
			eventsTime.addElement(new Time(300.0, TimeUnit.SECONDS));
			eventsTime.addElement(new Time(1000.0, TimeUnit.SECONDS));
			eventsTime.addElement(new Time(2000.0, TimeUnit.SECONDS));

			String modelURI = UserModel.URI;
			simParams.put(modelURI + ":" + UserModel.USER_EVENTS_PARAM , createUserScenario());
			
			//TV State Model
			modelURI = TVStateModel.URI;
			simParams.put(modelURI + ":" + TVStateModel.TVSTATE_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"TV Model - State",
							"Time (sec)",
							"State",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			//TV Consumption Model
			modelURI = TVConsumption.URI;
			simParams.put(modelURI + ":" + TVConsumption.TVCONS_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"TV Model - Consumption",
							"Time (sec)",
							"Consumption",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
						
			modelURI = FridgeV2Model.URI;
			simParams.put(modelURI + ":" + FridgeV2Model.FRIDGECONS_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Fridge - Consumption",
							"Time (sec)",
							"Consumption (watt)",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));

			se.setSimulationRunParameters(simParams) ;
			
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			long start = System.currentTimeMillis() ;
			se.doStandAloneSimulation(0.0, 5000) ;
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
	
	public static Set<ES_EventI> createUserScenario(){
		Set<ES_EventI> events = new HashSet<ES_EventI>();
		Time t = new Time(7.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(100, TimeUnit.SECONDS))));
		events.add(new TVSwitch(new Time(200, TimeUnit.SECONDS)));
		//events.add(new TVSwitch(new Time(1000.0, TimeUnit.SECONDS)));
		t = new Time(3000.0, TimeUnit.SECONDS);
		events.add(new OpenDoor(t));
		events.add(new CloseDoor(t.add(new Duration(500.0, TimeUnit.SECONDS))));
		return events;
	}

}
