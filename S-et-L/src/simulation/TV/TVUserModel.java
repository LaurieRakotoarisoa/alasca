package simulation.TV;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.TV.events.TVSwitch;

@ModelExternalEvents(exported = {TVSwitch.class})
public class TVUserModel 
extends AtomicES_Model{
	
	public static class TVUserModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<TVSwitch>	readings ;

		public			TVUserModelReport(
			String modelURI,
			Vector<TVSwitch> readings
			)
		{
			super(modelURI) ;
			this.readings = readings ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "TV User Model Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of switch = " + this.readings.size() + "\n" ;
			ret += "Switch:\n" ;
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}
	
	/** Vector containing the times when TV is turning on or turning off */
	private Vector<Time> eventsTime;
	
	/** events sent	 */
	private Vector<TVSwitch> sent;
	
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "TV-USER";
	
	public static final String USER_EVENTS = "switch-event";

	public TVUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
		
		sent = new Vector<TVSwitch>();
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime) {
		super.initialiseState(initialTime) ;

		// Schedule the first Economy Event
		Time occurrence = eventsTime.remove(0);
		this.scheduleEvent(new TVSwitch(occurrence)) ;
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
					this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;

		// for atomic event scheduling models, the internal transition is
		// called when an event that must be exported occurs, hence it is
		// called here when the previous economy event has been sent so we must
		// schedule the next one.
		if(!eventsTime.isEmpty()) {
			this.logMessage(this.getCurrentStateTime() + "|TV Switch sent.") ;
			// Schedule the next TicEvent after the prescribed delay
			Time occurrence = this.getCurrentStateTime().add(new Duration(eventsTime.remove(0).getSimulatedTime(), this.getSimulatedTimeUnit()));
			this.scheduleEvent(new TVSwitch(occurrence)) ;
		}

		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname = this.getURI() + ":" + USER_EVENTS ;
		this.eventsTime = (Vector<Time>) simParams.get(vname);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new TVUserModelReport(this.getURI(),sent);
	}

}
