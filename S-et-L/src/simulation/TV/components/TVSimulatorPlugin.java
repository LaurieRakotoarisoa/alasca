package simulation.TV.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.TV.models.TVConsumption;
import simulation.TV.models.TVStateModel;

public class TVSimulatorPlugin 
extends AtomicSimulatorPlugin{

	private static final long serialVersionUID = 1L;
	
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
		
		if(m instanceof TVConsumption) {
			assert name.equals("consumption");
			return ((TVConsumption)m).getCons();
		}
		else {
			assert m instanceof TVStateModel && name.equals("backlight");
			return ((TVStateModel)m).getBacklight();
			
		}
	}

}
