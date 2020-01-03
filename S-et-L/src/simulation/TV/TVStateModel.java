package simulation.TV;

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
import simulation.AtomicModels.events.TvStateEvent;
import simulation.TV.events.TVSwitch;
import utils.TVMode;

@ModelExternalEvents(imported = {TVSwitch.class},
					exported = {TvStateEvent.class})
public class TVStateModel 
extends AtomicModel{
	
	public static class TVStateModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<TvStateEvent>	readings ;

		public			TVStateModelReport(
			String modelURI,
			Vector<TvStateEvent> readings
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
			ret += "TV State Model Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of changes = " + this.readings.size() + "\n" ;
			ret += "Changes of state :\n" ;
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}

	public TVStateModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		receivedEvent = false;
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
		states = new Vector<TvStateEvent>();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "TV-STATE";
	
	protected boolean receivedEvent;
	
	protected Vector<TvStateEvent> states;
	
	protected TVMode currentState;

	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = new Vector<EventI>();
		if(receivedEvent) {
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
			TvStateEvent e = new TvStateEvent(t,currentState);
			states.addElement(e);
			this.logMessage(e.eventAsString());
			receivedEvent = false;
		}
		return ret;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = TVMode.Off;
		super.initialiseState(initialTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.logMessage("at internal transition " +
							this.getCurrentStateTime().getSimulatedTime() +
							" " + elapsedTime.getSimulatedDuration()) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;
		Vector<EventI> current = this.getStoredEventAndReset();
		assert current != null & current.size() == 1;
		if(current.get(0) instanceof TVSwitch) 
			switchState();
			receivedEvent = true;
		
	}

	@Override
	public Duration timeAdvance() {
		return new Duration(60.0, TimeUnit.SECONDS);
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new TVStateModelReport(this.getURI(),states);
	}
	
	private void switchState() {
		if(currentState == TVMode.Off) currentState = TVMode.On;
		else if(currentState == TVMode.On) {currentState = TVMode.Off;} 
	}

}
