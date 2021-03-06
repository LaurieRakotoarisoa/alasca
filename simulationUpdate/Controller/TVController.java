package simulation.Controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;

@ModelExternalEvents(exported = { EconomyEvent.class, 
								NoEconomyEvent.class })
public class TVController 
extends AtomicES_Model{

	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** name of the run parameter defining the delay between economy events.	*/
	public static final String	DELAY_PARAMETER_NAME = "delay" ;
	/** the standard delay between economy events.								*/
	public static Duration		STANDARD_DURATION =
										new Duration(500.0, TimeUnit.SECONDS) ;
	/** the URI to be used when creating the instance of the model.			*/
	public static final String	URI = "TV-Controller" ;
	/** the value of the delay between economy events during the current
	 *  simulation run.														*/
	protected Duration			delay ;
	
	/** true if model has sent an economy event */
	protected boolean ecoActivated;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of TVController model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
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
	public				TVController(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.delay = TVController.STANDARD_DURATION ;
	}
	
	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	
	{
		this.ecoActivated = false;
		super.initialiseState(initialTime) ;

		// Schedule the first Economy Event
		Time occurrence = initialTime.add(this.delay) ;
		this.scheduleEvent(new EconomyEvent(occurrence)) ;
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
					this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String varName =
					this.getURI() + ":" + TVController.DELAY_PARAMETER_NAME ;
		if (simParams.containsKey(varName)) {
			this.delay = (Duration) simParams.get(varName) ;
		}
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
		
		if(ecoActivated) {
			this.logMessage(this.getCurrentStateTime() + "|NoEconomy event sent.") ;
			this.ecoActivated = false;
			// Schedule the next TicEvent after the prescribed delay.
			Time occurrence = this.getCurrentStateTime().add(this.delay) ;
			this.scheduleEvent(new NoEconomyEvent(occurrence)) ;
		}
		else {
			this.logMessage(this.getCurrentStateTime() + "|economy event sent.") ;
			this.ecoActivated = true;
			// Schedule the next TicEvent after the prescribed delay.
			Time occurrence = this.getCurrentStateTime().add(this.delay) ;
			this.scheduleEvent(new EconomyEvent(occurrence)) ;
		}

		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	throws Exception
	{
		final String uri = this.getURI() ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return "TVController_ModelReport()" ;
					}

					@Override
					public String getModelURI() {
						return uri ;
					}
			   } ;
	}

}
