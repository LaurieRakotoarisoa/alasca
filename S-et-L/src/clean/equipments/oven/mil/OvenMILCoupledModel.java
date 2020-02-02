package clean.equipments.oven.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import clean.environment.UserMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.oven.events.ActiveCompressor;
import simulation.oven.events.InactiveCompressor;

public class OvenMILCoupledModel 
extends CoupledModel
implements SGMILModelImplementationI{
	
	public OvenMILCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections);
	}

	private static final long serialVersionUID = 1L;
	
	public static final String	URI = OvenMILCoupledModel.class.getName();
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI#disposePlotters()
	 */
	@Override
	public void			disposePlotters() throws Exception
	{
		for (int i = 0 ; i < this.submodels.length ; i++) {
			ModelDescriptionI m =
					this.submodels[i].getDescendentModel(
												this.submodels[i].getURI()) ;
			if(m instanceof SGMILModelImplementationI) { 
				((SGMILModelImplementationI)m).disposePlotters() ;
			}
		}
	}
	
	/**
	 * build the simulation architecture corresponding to this coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the simulation architecture corresponding to this coupled model.
	 * @throws Exception	<i>TO DO.</i>
	 */
	public static Architecture	buildArchitecture() throws Exception
	{		
		
		return new Architecture(
				OvenMILCoupledModel.URI,
				createAtomicModelDescriptors(),
				createCoupledModelDescriptors(),
				TimeUnit.SECONDS);
			
	
	}

	public static Map<String, CoupledModelDescriptor> createCoupledModelDescriptors() {
		
		Map<Class<? extends EventI>,EventSink[]> imported =
				new HashMap<Class<? extends EventI>,EventSink[]>() ;
		
		imported.put(EconomyEvent.class,
				new EventSink[] {
						new EventSink(OvenStateMILModel.URI,
								EconomyEvent.class)});
		imported.put(NoEconomyEvent.class,
				new EventSink[] {
						new EventSink(OvenStateMILModel.URI,
								NoEconomyEvent.class)});
		
		
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<String,CoupledModelDescriptor>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(OvenConsumptionMILModel.URI) ;
		submodels.add(OvenStateMILModel.URI) ;
		submodels.add(UserMILModel.URI);
		
		
		
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>() ;
		
		EventSource from1 =
		new EventSource(OvenStateMILModel.URI, ActiveCompressor.class) ;
		EventSink[] to1 = new EventSink[] {
		new EventSink(OvenConsumptionMILModel.URI, ActiveCompressor.class)} ;
		connections.put(from1, to1) ;
		
		EventSource from2 =
		new EventSource(OvenStateMILModel.URI, InactiveCompressor.class) ;
		EventSink[] to2 = new EventSink[] {
		new EventSink(OvenConsumptionMILModel.URI, InactiveCompressor.class)} ;
		connections.put(from2, to2) ;
		
		coupledModelDescriptors.put(
				OvenMILCoupledModel.URI,
				new CoupledModelDescriptor(
						OvenMILCoupledModel.class,
						OvenMILCoupledModel.URI,
						submodels,
						imported,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE)) ;
		
		return coupledModelDescriptors;
		
	}

	public static Map<String, AbstractAtomicModelDescriptor> createAtomicModelDescriptors() throws Exception {
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		
		atomicModelDescriptors.put(
				OvenConsumptionMILModel.URI,
				AtomicModelDescriptor.create(
						OvenConsumptionMILModel.class,
						OvenConsumptionMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				OvenStateMILModel.URI,
				AtomicModelDescriptor.create(
						OvenStateMILModel.class,
						OvenStateMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				UserMILModel.URI,
				AtomicModelDescriptor.create(
						UserMILModel.class,
						UserMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

		return atomicModelDescriptors ;
	}

}
