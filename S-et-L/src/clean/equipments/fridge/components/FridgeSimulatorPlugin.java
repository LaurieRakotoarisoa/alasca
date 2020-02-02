package clean.equipments.fridge.components;



import java.util.Map;

import clean.equipments.fridge.mil.FridgeStateMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

public class FridgeSimulatorPlugin 
extends AtomicSimulatorPlugin{

	
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation() throws Exception
	{
		ModelDescriptionI m =
				this.simulator.getDescendentModel(this.simulator.getURI()) ;
		assert	m instanceof SGMILModelImplementationI ;
		((SGMILModelImplementationI)m).disposePlotters() ;
		super.finalise();
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Object		getModelStateValue(String modelURI, String name)
	throws Exception
	{
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI) ;
		// The only model in this example that provides access to some value
		// is the HairDryerModel.
		return ((FridgeStateMILModel)m).getStateDoor();
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		simParams.put(FridgeStateMILModel.COMPONENT_HOLDER_REF_PARAM_NAME,
				  this.owner) ;
	super.setSimulationRunParameters(simParams) ;
	}

}
