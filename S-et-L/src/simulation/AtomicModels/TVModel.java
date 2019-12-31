package simulation.AtomicModels;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.AtomicModels.events.TvStateEvent;
import utils.TVMode;


@ModelExternalEvents(exported = {TvStateEvent.class})
public class TVModel 
extends AtomicModel{
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** name of the run parameter defining the delay between tic events.	*/
	public static final String	DELAY_PARAMETER_NAME = "delay" ;
	/** the standard delay between tv changing mode.								*/
	public static Duration		STANDARD_DURATION =
										new Duration(60.0, TimeUnit.SECONDS) ;
	/** the URI to be used when creating the instance of the model.			*/
	public static final String	URI = "TVModel" ;
	/** the value of the delay between tv change events during the current
	 *  simulation run.														*/
	protected Duration			delay ;
	
	/** list of times when occurs tv state changes */
	protected Vector<Time> TVevents;
	
	/** current state of the TV */
	protected  TVMode currentState; 
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>todo.</i>
	 */
	public				TVModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.delay = TVModel.STANDARD_DURATION ;
		this.setLogger(new StandardLogger()) ;
		this.toggleDebugMode() ;
		this.TVevents = new Vector<Time>();
		this.TVevents.addElement(new Time(120.0, TimeUnit.SECONDS));
		this.TVevents.addElement(new Time(180.0, TimeUnit.SECONDS));
		this.currentState = TVMode.Off;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		Vector<EventI> ret = new Vector<EventI>() ;
		// compute the current simulation time because it has not been
		// updated yet.
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
		if(!TVevents.isEmpty() && t.equals(TVevents.get(0))) {
			TVevents.remove(0);
			TvStateEvent e = new TvStateEvent(t, changeState());
			this.logMessage("output " + e.eventAsString()) ;
			// create the external event.
			ret.add(e) ;
			// return the new tic event.
			return ret ;
		}
		return ret;
		
	}

	@Override
	public Duration timeAdvance() {
		return this.delay;
	}
	
	public TVMode changeState() {
		if(currentState == TVMode.On) {
			currentState =TVMode.Off;
		}
		else if(currentState == TVMode.Off) {
			currentState =TVMode.On;
		}
		return currentState;
	}

}
