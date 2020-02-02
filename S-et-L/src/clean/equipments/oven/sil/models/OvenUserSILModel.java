package clean.equipments.oven.sil.models;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import clean.equipments.oven.components.OvenComponent;
import clean.equipments.oven.mil.OvenStateMILModel;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class OvenUserSILModel 
extends AtomicES_Model{


	public OvenUserSILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	private static final long serialVersionUID = 1L;
	
	private OvenComponent componentRef;
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public OvenComponent	getComponentRef()
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
				(OvenComponent) simParams.get(OvenStateMILModel.
											COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}

}
