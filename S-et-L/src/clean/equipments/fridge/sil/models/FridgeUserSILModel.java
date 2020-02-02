package clean.equipments.fridge.sil.models;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import clean.equipments.fridge.components.FridgeComponent;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class FridgeUserSILModel 
extends AtomicES_Model{


	public FridgeUserSILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	private static final long serialVersionUID = 1L;
	
	private FridgeComponent componentRef;
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public FridgeComponent	getComponentRef()
	{
		return this.componentRef ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// The reference to the embedding component
		this.componentRef =
				(FridgeComponent) simParams.get(FridgeStateMILModel.
											COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}

}
