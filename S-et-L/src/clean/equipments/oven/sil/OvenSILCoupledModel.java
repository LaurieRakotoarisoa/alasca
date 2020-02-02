package clean.equipments.oven.sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import clean.equipments.oven.mil.OvenMILCoupledModel;
import clean.equipments.oven.sil.models.OvenStateSILModel;
import clean.equipments.oven.sil.models.UserOvenSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILCoupledModel;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class OvenSILCoupledModel 
extends OvenMILCoupledModel{

	
	public OvenSILCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	public static final String URI = OvenSILCoupledModel.class.getName();
	
	public static Map<String, AbstractAtomicModelDescriptor> createAtomicModelDescriptors() throws Exception {
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		
		atomicModelDescriptors.put(
				OvenStateSILModel.URI,
				AtomicModelDescriptor.create(
						OvenStateSILModel.class,
						OvenStateSILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				UserOvenSILModel.URI,
				AtomicModelDescriptor.create(
						OvenStateSILModel.class,
						OvenStateSILModel.URI,
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
		submodels.add(UserOvenSILModel.URI) ;
		submodels.add(OvenStateSILModel.URI) ;

		coupledModelDescriptors.put(
				HairDryerSILCoupledModel.URI,
				new CoupledModelDescriptor(
						OvenSILCoupledModel.class,
						OvenSILCoupledModel.URI,
						submodels,
						null,
						null,
						null,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE)) ;

		return coupledModelDescriptors ;
	}
	

}
