package simulation.TV;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.TV.events.TVSwitch;

@ModelExternalEvents(exported = {TVSwitch.class})
public class TVUserModel 
extends AtomicModel{
	
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

	

	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = new Vector<EventI>() ;
		// compute the current simulation time because it has not been
		// updated yet.
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
		if(!eventsTime.isEmpty() && eventsTime.get(0).equals(t)) {
			eventsTime.remove(0);
			TVSwitch event = new TVSwitch(t);
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
