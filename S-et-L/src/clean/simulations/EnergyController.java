package clean.simulations;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
/**
 * Coupled Model for the assembly of all equipments in the house
 * @author Laurie
 *
 */
public class EnergyController 
extends CoupledModel
implements SGMILModelImplementationI{

	public EnergyController(
			String uri,
			TimeUnit simulatedTimeUnit,
			SimulatorI simulationEngine,
			ModelDescriptionI[] submodels,
			Map<Class<? extends EventI>,
			EventSink[]> imported,
			Map<Class<? extends EventI>,
			ReexportedEvent> reexported,
			Map<EventSource,EventSink[]> connections
			) throws Exception
		{
			super(uri, simulatedTimeUnit, simulationEngine, submodels,
				  imported, reexported, connections) ;
		}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Energy-Controller";
	
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

}
