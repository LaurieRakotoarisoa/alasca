package simulation.oven.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.oven.events.OvenSwitchEvent;

@ModelExternalEvents(exported = {OvenSwitchEvent.class})
public class OvenUserModel 
extends AtomicModel{
	
	public static class OvenUserModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<OvenSwitchEvent>	readings ;

		public			OvenUserModelReport(
			String modelURI,
			Vector<OvenSwitchEvent> readings
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
	private Vector<OvenSwitchEvent> sent;
	
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Oven-USER";
	
	public static final String USER_EVENTS = "oven-switch-event";

	public OvenUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
		
		sent = new Vector<OvenSwitchEvent>();
	}

	@Override
	public ArrayList<EventI> output() {
		ArrayList<EventI> ret = new ArrayList<EventI>() ;
		// compute the current simulation time because it has not been
		// updated yet.
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
		if(!eventsTime.isEmpty() && eventsTime.get(0).equals(t)) {
			eventsTime.remove(0);
			OvenSwitchEvent event = new OvenSwitchEvent(t);
			this.logMessage(event.eventAsString());
			ret.add(event);
			sent.addElement(event);
		}
		return ret;
	}

	@Override
	public Duration timeAdvance() {
		if(!eventsTime.isEmpty())
			//time advance to the next event scheduled if it exists
			return eventsTime.get(0).subtract(this.getCurrentStateTime());
		return Duration.INFINITY;
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
		return new OvenUserModelReport(this.getURI(),sent);
	}

}
