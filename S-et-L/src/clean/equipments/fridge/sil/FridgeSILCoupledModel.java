package clean.equipments.fridge.sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.fridge.sil.models.FridgeStateSILModel;
import clean.equipments.fridge.sil.models.UserFridgeSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILUserModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class FridgeSILCoupledModel 
extends FridgeMILCoupledModel{

	
	public FridgeSILCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = FridgeSILCoupledModel.class.getName();
	
	public static Map<String, AbstractAtomicModelDescriptor> createAtomicModelDescriptors() throws Exception {
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		
		atomicModelDescriptors.put(
				FridgeStateSILModel.URI,
				AtomicModelDescriptor.create(
						FridgeStateSILModel.class,
						FridgeStateSILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				UserFridgeSILModel.URI,
				AtomicModelDescriptor.create(
						FridgeStateSILModel.class,
						FridgeStateSILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

		return atomicModelDescriptors ;
	}
	
	/**
	 * create the coupled model descriptors for all coupled models having this
	 * coupled model as ancestor and also including this coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the coupled model descriptors of this model and its descendants.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Map<String,CoupledModelDescriptor>
												createCoupledModelDescriptors()
	{
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
								new HashMap<String,CoupledModelDescriptor>() ;


		Set<String> submodels = new HashSet<String>() ;
		submodels.add(UserFridgeSILModel.URI) ;
		submodels.add(FridgeStateSILModel.URI) ;

		coupledModelDescriptors.put(
				HairDryerSILCoupledModel.URI,
				new CoupledModelDescriptor(
						FridgeSILCoupledModel.class,
						FridgeSILCoupledModel.URI,
						submodels,
						null,
						null,
						null,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE)) ;

		return coupledModelDescriptors ;
	}
	

}
