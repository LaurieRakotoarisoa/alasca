package clean.equipments.oven.sil.models;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import clean.environment.UserScenarii;
import clean.equipments.oven.mil.OvenStateMILModel;
import components.device.Oven;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class UserOvenSILModel
extends		AtomicES_Model
implements	SGMILModelImplementationI
{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public UserOvenSILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	private static final long	serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example).			*/
	public static final String	URI = UserOvenSILModel.class.getName() ;
	
	/** next event to be sent.												*/
	protected Class<?>	nextEvent ;
	
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected Oven componentRef ;
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public Oven getComponentRef()
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
				(Oven) simParams.get(OvenStateMILModel.
											COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime) 
	{
		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		
		//getting correct current Time
		super.initialiseState(initialTime) ;
		
		this.scheduleEvents(UserScenarii.createFridgeScenario());
		
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.nextTimeAdvance) ;
	}

}
