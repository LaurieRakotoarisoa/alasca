package simulation.oven.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.oven.models.OvenConsumptionMILModel;
import simulation.oven.models.OvenStateModel;

public class OvenSimulatorPlugin 
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
		
		if(m instanceof OvenConsumptionMILModel) {
			assert name.equals("consumption");
			return ((OvenConsumptionMILModel)m).getCons();
		}
		else {
			assert m instanceof OvenStateModel && name.equals("backlight");
			return ((OvenStateModel)m).getTemperature();
			
		}
	}

}
