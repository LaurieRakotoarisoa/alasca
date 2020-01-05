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
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.Controller.EnergyController;
import simulation.Controller.TVController;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.Fridge.FridgeTemperature;
import simulation.TV.events.TVSwitch;
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
			
			atomicModelDescriptors.put(TicModel.URI,
					AtomicModelDescriptor.create(TicModel.class,
							TicModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(TVConsumption.URI,
					AtomicModelDescriptor.create(TVConsumption.class,
							TVConsumption.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels1 = new HashSet<String>() ;
			submodels1.add(TVUserModel.URI);
			submodels1.add(TVStateModel.URI);
			submodels1.add(TicModel.URI);
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
					
			Map<Class<? extends EventI>,ReexportedEvent> reexported1 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			
			Map<EventSource,EventSink[]> connections1 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from11 =
					new EventSource(TVUserModel.URI,
									TVSwitch.class) ;
			EventSink[] to11 =
					new EventSink[] {
						new EventSink(TVStateModel.URI,
									  TVSwitch.class)} ;
			connections1.put(from11, to11) ;
			
			EventSource from12 =
					new EventSource(TicModel.URI,
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
			atomicModelDescriptors.put(TVController.URI,
			AtomicModelDescriptor.create(TVController.class,
					TVController.URI,
					TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Fridge Model
			// ----------------------------------------------------------------
			atomicModelDescriptors.put(FridgeTemperature.URI,
			AtomicModelDescriptor.create(FridgeTemperature.class,
					FridgeTemperature.URI,
					TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Full architecture and Global model
			// ----------------------------------------------------------------
			Set<String> submodels2 = new HashSet<String>() ;
			submodels2.add(TVModel.URI);
			submodels2.add(TVController.URI);
			submodels2.add(FridgeTemperature.URI);
			
			Map<EventSource,EventSink[]> connections2 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from21 =
					new EventSource(
							TVController.URI,
							EconomyEvent.class) ;
			EventSink[] to21 =
					new EventSink[] {
							new EventSink(
									TVModel.URI,
									EconomyEvent.class)} ;
			connections2.put(from21, to21) ;
			
			EventSource from22 =
					new EventSource(
							TVController.URI,
							NoEconomyEvent.class) ;
			EventSink[] to22 =
					new EventSink[] {
							new EventSink(
									TVModel.URI,
									NoEconomyEvent.class)} ;
			connections2.put(from22, to22) ;
			
			coupledModelDescriptors.put(
					EnergyController.URI,
					new CoupledModelDescriptor(
							EnergyController.class,
							MoleneModel.URI,
							submodels2,
							null,
							null,
							connections2,
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

			String modelURI = TVUserModel.URI;
			simParams.put(modelURI + ":" + TVUserModel.USER_EVENTS , eventsTime);
			
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
			
			// Fridge Temperature
			modelURI = FridgeTemperature.URI;
			simParams.put(modelURI + ":" + FridgeTemperature.FRIDGE_TEMP_PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Fridge - Temperature",
							"Time (sec)",
							"Temperature",
							SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
							SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight()));
			

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
