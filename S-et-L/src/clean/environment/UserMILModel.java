package clean.environment;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.OpenDoor;
import simulation.TV.events.TVSwitch;
import simulation.oven.events.OvenSwitchEvent;

@ModelExternalEvents(exported = {TVSwitch.class, OpenDoor.class, CloseDoor.class, OvenSwitchEvent.class})
public class UserMILModel 
extends AtomicES_Model{
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	

	public static class UserModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Set<ES_EventI>	events ;

		public			UserModelReport(
			String modelURI,
			Set<ES_EventI> events
			)
		{
			super(modelURI) ;
			this.events = events ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "User Model Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of actions = " + this.events.size() + "\n" ;
			ret += "Switch:\n" ;
			for (ES_EventI e  : this.events) {
				ret += "    " + e.eventAsString() + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	public static final String USER_EVENTS_PARAM = "user-events";
	
	public static final String URI = UserMILModel.class.getName();
	
	protected Set<ES_EventI> user_events;

	public UserMILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime) {
		
		user_events = UserScenarii.createGlobalScenario();
		
		super.initialiseState(initialTime) ;
		
		if(user_events != null && !user_events.isEmpty()) {
			this.scheduleEvents(user_events);
			this.nextTimeAdvance = this.timeAdvance() ;
			this.timeOfNextEvent =
						this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
		}
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new UserModelReport(this.getURI(),user_events);
	}

}
