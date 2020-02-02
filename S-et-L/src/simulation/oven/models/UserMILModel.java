package simulation.oven.models;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.oven.events.OvenSwitchEvent;

@ModelExternalEvents(exported = {OvenSwitchEvent.class})
public class UserMILModel 
extends AtomicES_Model{
	
	public static class OvenUserModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final ArrayList<OvenSwitchEvent>	readings ;

		public			OvenUserModelReport(
			String modelURI,
			ArrayList<OvenSwitchEvent> readings
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
			ret += "Oven User Model Report\n" ;
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
	
	/** Vector containing the times when Oven is turning on or turning off */
	private Vector<Time> eventsTime;
	
	/** events sent	 */
	private ArrayList<OvenSwitchEvent> sent;
	
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Oven-USER";
	
	public static final String USER_EVENTS = "switch-event";
	
	private RandomDataGenerator rg;

	public UserMILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
		
		sent = new ArrayList<OvenSwitchEvent>();
		rg = new RandomDataGenerator();
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime) {
		
		rg.reSeed();
		super.initialiseState(initialTime) ;

		// Schedule the first Economy Event
		Time occurrence = initialTime.add(new Duration(rg.nextUniform(30, 500, false),TimeUnit.SECONDS));
		this.scheduleEvent(new OvenSwitchEvent(occurrence)) ;
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
		Time time = this.getCurrentStateTime().add(new Duration(1, TimeUnit.SECONDS));
		this.logMessage(time + "|Oven Switch sent.") ;
		// Schedule the next TicEvent after the prescribed delay
		this.scheduleEvent(newOvenSwitchEventEvent(time));
		
	}
	
	public OvenSwitchEvent newOvenSwitchEventEvent(Time current) {
		Time newTime = current.add(new Duration(rg.nextUniform(200, 1000), TimeUnit.SECONDS));
		return new OvenSwitchEvent(newTime);
	}
	
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new OvenUserModelReport(this.getURI(),sent);
	}

}
