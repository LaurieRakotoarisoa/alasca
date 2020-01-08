package simulation.oven.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.MoleneModel;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.Controller.EnergyController;
import simulation.Controller.OvenController;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.oven.events.OvenSwitchEvent;

public class OvenModel
extends CoupledModel{

	public OvenModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String	URI = "Oven-MODEL" ;

	public static Architecture getArchitecture() {
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		try {
			
			atomicModelDescriptors.put(OvenUserModel.URI,
					AtomicModelDescriptor.create(OvenUserModel.class,
							OvenUserModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(OvenStateModel.URI,
					AtomicModelDescriptor.create(OvenStateModel.class,
							OvenStateModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TicModel.URI,
					AtomicModelDescriptor.create(TicModel.class,
							TicModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(OvenConsumptionModel.URI,
					AtomicModelDescriptor.create(OvenConsumptionModel.class,
							OvenConsumptionModel.URI,
							TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels1 = new HashSet<String>() ;
			submodels1.add(OvenUserModel.URI);
			submodels1.add(OvenStateModel.URI);
			submodels1.add(TicModel.URI);
			submodels1.add(OvenConsumptionModel.URI);
			
			Map<Class<? extends EventI>,EventSink[]> imported1 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
					
			imported1.put(EconomyEvent.class,
					new EventSink[] {
							new EventSink(OvenStateModel.URI,
									EconomyEvent.class)});
			
			imported1.put(NoEconomyEvent.class,
					new EventSink[] {
							new EventSink(OvenStateModel.URI,
									NoEconomyEvent.class)});
					
			Map<Class<? extends EventI>,ReexportedEvent> reexported1 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
//			reexported1.put(
//					OvenStateEvent.class,
//					new ReexportedEvent(OvenStateModel.URI,
//										OvenStateEvent.class)) ;
			
			Map<EventSource,EventSink[]> connections1 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from11 =
					new EventSource(OvenUserModel.URI,
									OvenSwitchEvent.class) ;
			EventSink[] to11 =
					new EventSink[] {
						new EventSink(OvenStateModel.URI,
									  OvenSwitchEvent.class)} ;
			connections1.put(from11, to11) ;
			
			EventSource from12 =
					new EventSource(TicModel.URI,
									TicEvent.class) ;
			EventSink[] to12 =
					new EventSink[] {
						new EventSink(OvenConsumptionModel.URI,
									  TicEvent.class)} ;
			connections1.put(from12, to12) ;
			
			Map<VariableSource,VariableSink[]> bindings1 =
					new HashMap<VariableSource,VariableSink[]>() ;
					
			VariableSource source11 =
					new VariableSource("temperature",
									   Double.class,
									   OvenStateModel.URI) ;
				VariableSink[] sinks11 =
					new VariableSink[] {
							new VariableSink("temperature",
											 Double.class,
											 OvenConsumptionModel.URI)} ;
					
			bindings1.put(source11, sinks11);
			
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
					new HashMap<>() ;
			
			coupledModelDescriptors.put(
					OvenModel.URI,
					new CoupledHIOA_Descriptor(
							OvenModel.class,
							OvenModel.URI,
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
			atomicModelDescriptors.put(OvenController.URI,
			AtomicModelDescriptor.create(OvenController.class,
					OvenController.URI,
					TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Full architecture and Global model
			// ----------------------------------------------------------------
			Set<String> submodels2 = new HashSet<String>() ;
			submodels2.add(OvenModel.URI);
			submodels2.add(OvenController.URI);
			
			Map<EventSource,EventSink[]> connections2 =
					new HashMap<EventSource,EventSink[]>() ;
					
			EventSource from21 =
					new EventSource(
							OvenController.URI,
							EconomyEvent.class) ;
			EventSink[] to21 =
					new EventSink[] {
							new EventSink(
									OvenModel.URI,
									EconomyEvent.class)} ;
			connections2.put(from21, to21) ;
			
			EventSource from22 =
					new EventSource(
							OvenController.URI,
							NoEconomyEvent.class) ;
			EventSink[] to22 =
					new EventSink[] {
							new EventSink(
									OvenModel.URI,
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

			return new Architecture(
							EnergyController.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Object> getSettingRunParameters() {
		
		//Setting run parameters
		Map<String, Object> simParams = new HashMap<String, Object>() ;
		
		//Oven User Model
		Vector<Time> eventsTime = new Vector<Time>();
		eventsTime.addElement(new Time(90.0, TimeUnit.SECONDS));
		eventsTime.addElement(new Time(180.0, TimeUnit.SECONDS));
		eventsTime.addElement(new Time(300.0, TimeUnit.SECONDS));
		eventsTime.addElement(new Time(1000.0, TimeUnit.SECONDS));
		eventsTime.addElement(new Time(2000.0, TimeUnit.SECONDS));

		String modelURI = OvenUserModel.URI;
		simParams.put(modelURI + ":" + OvenUserModel.USER_EVENTS , eventsTime);
		
		//Oven State Model
		modelURI = OvenStateModel.URI;
		simParams.put(modelURI + ":" + OvenStateModel.OVENSTATE_PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Oven Model - State",
						"Time (sec)",
						"State",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));
		
		//Oven Consumption Model
		modelURI = OvenConsumptionModel.URI;
		simParams.put(modelURI + ":" + OvenConsumptionModel.OVENCONS_PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"OVEN Model - Consumption",
						"Time (sec)",
						"Consumption",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));
		
		return simParams;
	}

}
