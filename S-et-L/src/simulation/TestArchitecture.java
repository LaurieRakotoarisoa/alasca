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
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.Counter.CounterModel;
import simulation.Fridge.events.ActiveCompressor;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.FridgeConsumptionEvent;
import simulation.Fridge.events.InactiveCompressor;
import simulation.Fridge.events.OpenDoor;
import simulation.Fridge.models2.FridgeConsumption;
import simulation.Fridge.models2.FridgeCoupledModel;
import simulation.Fridge.models2.FridgeState;
import simulation.TV.events.TVConsumptionEvent;
import simulation.TV.events.TVSwitch;
import simulation.TV.models.TVConsumption;
import simulation.TV.models.TVMILCoupledModel;
import simulation.TV.models.TVStateModel;
import simulation.environment.UserModel;
import simulation.environment.UserScenarii;
import simulation.environment.electricity.Electricity_ESModel;
import simulation.environment.electricity.events.RestoreElecEvent;
import simulation.environment.electricity.events.SheddingEvent;
import simulation.environment.wind.events.ModerateWindEvent;
import simulation.environment.wind.events.WaftEvent;
import simulation.environment.wind.events.WindGustsEvent;
import simulation.environment.wind.models.WindModel;
import simulation.oven.events.OvenSwitchEvent;
import simulation.oven.models.OvenConsumptionModel;
import simulation.oven.models.OvenModel;
import simulation.oven.models.OvenStateModel;
import simulation.windTurbine.models.WindTurbineModel;
public class TestArchitecture{

	public static void main(String[] args) {
		
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		try {
			
				
			// ----------------------------------------------------------------
			// TV Model 
			// ----------------------------------------------------------------
			
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
			reexported1.put(
					TVConsumptionEvent.class,
					new ReexportedEvent(TVConsumption.URI,
										TVConsumptionEvent.class)) ;
			
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
					TVMILCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							TVMILCoupledModel.class,
							TVMILCoupledModel.URI,
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
			// Fridge Model 
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(FridgeState.URI,
					AtomicModelDescriptor.create(FridgeState.class,
							FridgeState.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(FridgeConsumption.URI,
					AtomicModelDescriptor.create(FridgeConsumption.class,
							FridgeConsumption.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels4 = new HashSet<String>() ;
			submodels4.add(FridgeState.URI);
			submodels4.add(FridgeConsumption.URI);
			
			Map<Class<? extends EventI>,EventSink[]> imported4 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
					
			imported4.put(EconomyEvent.class,
					new EventSink[] {
							new EventSink(FridgeState.URI,
									EconomyEvent.class)});
			imported4.put(NoEconomyEvent.class,
					new EventSink[] {
							new EventSink(FridgeState.URI,
									NoEconomyEvent.class)});
			
			imported4.put(CloseDoor.class,
					new EventSink[] {
							new EventSink(FridgeState.URI,
									CloseDoor.class)});
			imported4.put(OpenDoor.class,
					new EventSink[] {
							new EventSink(FridgeState.URI,
									OpenDoor.class)});
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported4 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported4.put(
					FridgeConsumptionEvent.class,
					new ReexportedEvent(FridgeConsumption.URI,
										FridgeConsumptionEvent.class)) ;
			
			Map<EventSource,EventSink[]> connections4 =
					new HashMap<EventSource,EventSink[]>() ;
			
			EventSource from41 =
					new EventSource(FridgeState.URI,
									ActiveCompressor.class) ;
			EventSink[] to41 =
					new EventSink[] {
						new EventSink(FridgeConsumption.URI,
									  ActiveCompressor.class)} ;
			connections4.put(from41, to41) ;
			
			EventSource from42 =
					new EventSource(FridgeState.URI,
									InactiveCompressor.class) ;
			EventSink[] to42 =
					new EventSink[] {
						new EventSink(FridgeConsumption.URI,
									  InactiveCompressor.class)} ;
			connections4.put(from42, to42) ;
			
			coupledModelDescriptors.put(
					FridgeCoupledModel.URI,
					new CoupledModelDescriptor(
							FridgeCoupledModel.class,
							FridgeCoupledModel.URI,
							submodels4,
							imported4,
							reexported4,
							connections4,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE)) ;
			
			
			// ----------------------------------------------------------------
			// OvenModel
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(OvenStateModel.URI,
					AtomicModelDescriptor.create(OvenStateModel.class,
							OvenStateModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(OvenConsumptionModel.URI,
					AtomicModelDescriptor.create(OvenConsumptionModel.class,
							OvenConsumptionModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));	
			
			atomicModelDescriptors.put(TicModel.URI+"-2",
					AtomicModelDescriptor.create(TicModel.class,
							TicModel.URI+"-2",
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			
			Set<String> submodels2 = new HashSet<String>() ;
			submodels2.add(OvenStateModel.URI);
			submodels2.add(OvenConsumptionModel.URI);
			submodels2.add(TicModel.URI+"-2");
			
			Map<Class<? extends EventI>,EventSink[]> imported2 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
					
			imported2.put(EconomyEvent.class,
					new EventSink[] {
							new EventSink(OvenStateModel.URI,
									EconomyEvent.class)});
			imported2.put(NoEconomyEvent.class,
					new EventSink[] {
							new EventSink(OvenStateModel.URI,
									NoEconomyEvent.class)});
			
			imported2.put(OvenSwitchEvent.class,
					new EventSink[] {
							new EventSink(OvenStateModel.URI,
									OvenSwitchEvent.class)});
			
			Map<EventSource,EventSink[]> connections2 =
					new HashMap<EventSource,EventSink[]>() ;
					
					EventSource from21 =
							new EventSource(TicModel.URI+"-2",
											TicEvent.class) ;
					EventSink[] to21 =
							new EventSink[] {
								new EventSink(OvenConsumptionModel.URI,
											  TicEvent.class)} ;
					connections2.put(from21, to21) ;
					
					
					
			Map<VariableSource,VariableSink[]> bindings2 =
					new HashMap<VariableSource,VariableSink[]>() ;
					
			VariableSource source21 =
					new VariableSource("temperature",
									   Double.class,
									   OvenStateModel.URI) ;
				VariableSink[] sinks21 =
					new VariableSink[] {
							new VariableSink("temperature",
											 Double.class,
											 OvenConsumptionModel.URI)} ;
				
				bindings2.put(source21, sinks21);
				
				coupledModelDescriptors.put(
						OvenModel.URI,
						new CoupledHIOA_Descriptor(
								OvenModel.class,
								OvenModel.URI,
								submodels2,
								imported2,
								null,
								connections2,
								null,
								SimulationEngineCreationMode.COORDINATION_ENGINE,
								null,
								null,
								bindings2)) ;
			
			
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
			// Wind Turbine model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(WindTurbineModel.URI,
					AtomicModelDescriptor.create(WindTurbineModel.class,
							WindTurbineModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Wind model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(WindModel.URI,
					AtomicModelDescriptor.create(WindModel.class,
							WindModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// User model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(UserModel.URI,
					AtomicModelDescriptor.create(UserModel.class,
							UserModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Counter model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(CounterModel.URI,
					AtomicModelDescriptor.create(CounterModel.class,
							CounterModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Full architecture and Global model
			// ----------------------------------------------------------------
			Set<String> submodels3 = new HashSet<String>() ;
			submodels3.add(TVMILCoupledModel.URI);
			submodels3.add(FridgeCoupledModel.URI);
			submodels3.add(Electricity_ESModel.URI);
			submodels3.add(HomeController.URI);
			submodels3.add(UserModel.URI);
			submodels3.add(OvenModel.URI);
			submodels3.add(CounterModel.URI);
			submodels3.add(WindTurbineModel.URI);
			submodels3.add(WindModel.URI);
			
			Map<EventSource,EventSink[]> connections3 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from31 =
					new EventSource(
							HomeController.URI,
							EconomyEvent.class) ;
			EventSink[] to31 =
					new EventSink[] {
							new EventSink(
									TVMILCoupledModel.URI,
									EconomyEvent.class),
							new EventSink(
									OvenModel.URI,
									EconomyEvent.class),
							new EventSink(
									FridgeCoupledModel.URI,
									EconomyEvent.class)} ;
			connections3.put(from31, to31) ;
			
			EventSource from32 =
					new EventSource(
							HomeController.URI,
							NoEconomyEvent.class) ;
			EventSink[] to32 =
					new EventSink[] {
							new EventSink(
									TVMILCoupledModel.URI,
									NoEconomyEvent.class),
							new EventSink(
									OvenModel.URI,
									NoEconomyEvent.class),
							new EventSink(
									FridgeCoupledModel.URI,
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
									TVMILCoupledModel.URI,
									TVSwitch.class)} ;
			connections3.put(from35, to35) ;
			
			EventSource from36 =
					new EventSource(UserModel.URI,
									OpenDoor.class) ;
			EventSink[] to36 =
					new EventSink[] {
						new EventSink(FridgeCoupledModel.URI,
									  OpenDoor.class)} ;
			connections3.put(from36, to36) ;
			
			EventSource from37 =
					new EventSource(UserModel.URI,
									CloseDoor.class) ;
			EventSink[] to37 =
					new EventSink[] {
						new EventSink(FridgeCoupledModel.URI,
									  CloseDoor.class)} ;
			connections3.put(from37, to37) ;
			
			EventSource from38 =
					new EventSource(UserModel.URI,
									OvenSwitchEvent.class) ;
			EventSink[] to38 =
					new EventSink[] {
						new EventSink(OvenModel.URI,
									  OvenSwitchEvent.class)} ;
			connections3.put(from38, to38) ;
			
			EventSource from39 =
					new EventSource(FridgeCoupledModel.URI,
									FridgeConsumptionEvent.class) ;
			EventSink[] to39 =
					new EventSink[] {
						new EventSink(CounterModel.URI,
									  FridgeConsumptionEvent.class)} ;
			connections3.put(from39, to39) ;
			
			EventSource from310 =
					new EventSource(TVMILCoupledModel.URI,
									TVConsumptionEvent.class) ;
			EventSink[] to310 =
					new EventSink[] {
						new EventSink(CounterModel.URI,
									  TVConsumptionEvent.class)} ;
			
			connections3.put(from310, to310) ;
			
			EventSource from311 =
					new EventSource(WindModel.URI,
									WaftEvent.class) ;
			EventSink[] to311 =
					new EventSink[] {
						new EventSink(WindTurbineModel.URI,
									  WaftEvent.class)} ;
			
			connections3.put(from311, to311) ;
			
			EventSource from312 =
					new EventSource(WindModel.URI,
									ModerateWindEvent.class) ;
			EventSink[] to312 =
					new EventSink[] {
						new EventSink(WindTurbineModel.URI,
									  ModerateWindEvent.class)} ;
			
			connections3.put(from312, to312) ;
			
			EventSource from313 =
					new EventSource(WindModel.URI,
									WindGustsEvent.class) ;
			EventSink[] to313 =
					new EventSink[] {
						new EventSink(WindTurbineModel.URI,
									  WindGustsEvent.class)} ;
			
			connections3.put(from313, to313) ;
			
			
			
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
			simParams.put(modelURI + ":" + UserModel.USER_EVENTS_PARAM , UserScenarii.createGlobalScenario());
			
			//TV State Model
			modelURI = TVStateModel.URI;
			simParams.put(modelURI + ":" + TVStateModel.TVSTATE_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"TV Model - State",
							"Time (sec)",
							"State",
							SimulationMain.ORIGIN_X,
							SimulationMain.ORIGIN_Y,
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			//TV Consumption Model
			modelURI = TVConsumption.URI;
			simParams.put(modelURI + ":" + TVConsumption.TVCONS_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"TV Model - Consumption",
							"Time (sec)",
							"Consumption",
							SimulationMain.ORIGIN_X,
							SimulationMain.ORIGIN_Y +
								SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			//Fridge Model
			modelURI = FridgeState.URI;
			simParams.put(modelURI + ":" + FridgeState.FRIDGE_TEMP_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Fridge - Temperature",
							"Time (sec)",
							"Consumption (watt)",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y,
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			modelURI = FridgeConsumption.URI;
			simParams.put(modelURI + ":" + FridgeConsumption.FRIDGE_CONS_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Fridge - Consumption",
							"Time (sec)",
							"Consumption (kWh)",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			//Oven State Model
			modelURI = OvenStateModel.URI;
			simParams.put(modelURI + ":" + OvenStateModel.OVENSTATE_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Oven Model - State",
							"Time (sec)",
							"Consumption (Watt)",
							SimulationMain.ORIGIN_X +
						  		2*SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y,
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			//Oven cons Model
			modelURI = OvenConsumptionModel.URI;
			simParams.put(modelURI + ":" + OvenConsumptionModel.OVENCONS_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Oven Model - Consumption",
							"Time (sec)",
							"Consumption (Watt)",
							SimulationMain.ORIGIN_X +
						  		2*SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			
			//TICS
			modelURI = TicModel.URI  + "-1" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;

			modelURI = TicModel.URI  + "-2" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(45.0, TimeUnit.SECONDS)) ;
			
			//Counter model
			modelURI = CounterModel.URI;
			simParams.put(modelURI+":"+CounterModel.HOMECONS_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Counter Model - Home Consumption",
							"Time (sec)",
							"Consumption (Watt)",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			//Wind Turbine model
			modelURI = WindTurbineModel.URI;
			simParams.put(modelURI+":"+WindTurbineModel.TURBINE_ENERGY_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Wind Turbine Model - Wind Energy",
							"Time (sec)",
							"Production (Watt)",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								3 * SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			
			

			se.setSimulationRunParameters(simParams) ;
			
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			long start = System.currentTimeMillis() ;
			se.doStandAloneSimulation(0.0, 7000) ;
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
