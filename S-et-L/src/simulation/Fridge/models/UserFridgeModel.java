package simulation.Fridge.models;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.Fridge.actions.DoorAction;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.OpenDoor;

@ModelExternalEvents ( exported = { OpenDoor.class,
									CloseDoor.class })
public class UserFridgeModel 
extends AtomicES_Model{

	

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "user-fridge";
	
	/** Actions on fridge door to be simulated */
	private Vector<DoorAction> scheduledActions;
	
	/** run parameter to get the door actions for the simulation */ 
	public static final String DOOR_ACTION_PARAM = "door-actions";
	
	/** true if an OpenDoor event has been scheduled for the current action*/
	private boolean openedDoor;
	
	/** time duration of door opened or <code>null</code> if door hasn't been opened */ 
	private Duration durationOpening;
	
	
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public UserFridgeModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
	}
	
	
	// -------------------------------------------------------------------------
	// Simulation methods and protocol
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime) {
		
		this.openedDoor = false;
		this.durationOpening = null;
		
		super.initialiseState(initialTime) ;
		
		if(scheduledActions != null & !scheduledActions.isEmpty()) {
			scheduleDoorActions(scheduledActions.remove(0));
			this.nextTimeAdvance = this.timeAdvance() ;
			this.timeOfNextEvent =
						this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;
		this.logMessage("internal at "+ this.getCurrentStateTime().getSimulatedTime());
		if(scheduledActions != null & !scheduledActions.isEmpty()) {
			scheduleDoorActions(scheduledActions.remove(0));
		}
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname = this.getURI() + ":" + DOOR_ACTION_PARAM;
		scheduledActions = (Vector<DoorAction>) simParams.get(vname);
	}
	
	private void scheduleDoorActions(DoorAction action) {
		Set<ES_EventI> events = new HashSet<ES_EventI>();
		Time t1 = action.getBeginning();
		Time t2 = t1.add(action.getDuration());
		events.add(new OpenDoor(t1));			
		events.add(new CloseDoor(t2));
		this.scheduleEvents(events);
	}
	
	@Override
	public SimulationReportI		getFinalReport()
	throws Exception
	{
		final String uri = this.getURI() ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return "User_Fridge_ModelReport()" ;
					}

					@Override
					public String getModelURI() {
						return uri ;
					}
			   } ;
	}
}
