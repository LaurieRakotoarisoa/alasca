package fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example
// for the extension of the BCM component model that aims to define a components
// tailored for cyber-physical control systems (CPCS) for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetHigh;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOn;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.events.SetHighSIL;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.events.SetLowSIL;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.events.SwitchOffSIL;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.events.SwitchOnSIL;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerSILUserModel</code> implements a simple user
 * simulation model for the hair dryer example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In order to perform tit software-in-the-loop function, this model plans
 * events that say when to change the operating mode of the hair dryer. The
 * change itself is made by calling the corresponding services on the
 * component. They will change the current operating mode, which will then be
 * seen by the hair dryer SIL model, which in turn will update the electricity
 * consumption and send an event to the electric meter with the new consumption.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-01-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HairDryerSILUserModel
extends		AtomicES_Model
implements	SGMILModelImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example).			*/
	public static final String	URI = HairDryerSILUserModel.class.getName() ;

	/** initial delay before sending the first switch on event.				*/
	protected double	initialDelay ;
	/** delay between uses of the hair dryer from one day to another.		*/
	protected double	interdayDelay ;
	/** mean time between uses of the hair dryer in the same day.			*/
	protected double	meanTimeBetweenUsages ;
	/** during one use, mean time the hair dryer is at high temperature.	*/
	protected double	meanTimeAtHigh ;
	/** next event to be sent.												*/
	protected Class<?>	nextEvent ;

	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rg ;
	/** the current state of the hair dryer simulation model.				*/
	protected HairDryerMILModel.State 	hds ;

	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected HairDryer	componentRef ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer SIL user model instance.
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
	 * @throws Exception   			<i>TODO</i>.
	 */
	public				HairDryerSILUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator() ;

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public HairDryer	getComponentRef()
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
				(HairDryer) simParams.get(HairDryerMILModel.
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

		this.initialDelay = 10.0 ;
		this.interdayDelay = 100.0 ;
		this.meanTimeBetweenUsages = 10.0 ;
		this.meanTimeAtHigh = 4.0 ;
		this.hds = HairDryerMILModel.State.OFF ;

		this.rg.reSeedSecure() ;

		// Initialise to get the correct current time.
		super.initialiseState(initialTime) ;

		// Schedule the first SwitchOn event.
		Duration d1 = new Duration(
							this.initialDelay,
							this.getSimulatedTimeUnit()) ;
		Duration d2 =
			new Duration(
					2.0 * this.meanTimeBetweenUsages *
											this.rg.nextBeta(1.75, 1.75),
					this.getSimulatedTimeUnit()) ;
		Time t = this.getCurrentStateTime().add(d1).add(d2) ;
		this.scheduleEvent(this.createSwitchOnEvent(t)) ;

		// Redo the initialisation to take into account the initial event
		// just scheduled.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.nextTimeAdvance) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// This is just for debugging purposes; the time advance for an ES
		// model is given by the earliest time among the currently scheduled
		// events.
		Duration d = super.timeAdvance() ;
		this.logMessage("next event in " + d.getSimulatedDuration()
						+ " from list " + this.eventListAsString()) ;
		return d ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(
		Duration elapsedTime
		)
	{
		// This method implements a usage scenario for the hair dryer.
		// Here, we assume that the hair dryer is used once each cycle (day)
		// and then it starts in low mode, is set in high mode shortly after,
		// used for a while in high mode and then set back in low mode to
		// complete the drying.

		assert	this.eventList.size() >= 1 ;
		this.nextEvent = this.eventList.peek().getClass() ;

		Duration d ;
		// See what is the type of event to be executed
		if (SwitchOn.class.isAssignableFrom(this.nextEvent)) {
			// when a switch on event has been issued, plan the next event as
			// a set high (the hair dryer is switched on in low mode
			d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75),
							 this.getSimulatedTimeUnit()) ;
			// compute the time of occurrence (in the future)
			Time t = this.getCurrentStateTime().add(d) ;
			// schedule the event
			this.scheduleEvent(this.createSetHighEvent(t)) ;
			// also, plan the next switch on for the next day
			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit()) ;
			t = this.getCurrentStateTime().add(d) ;
			this.scheduleEvent(this.createSwitchOnEvent(t)) ;
		} else if (SetHigh.class.isAssignableFrom(this.nextEvent)) {
			// when a set high event has been issued, plan the next switch off
			// after some time of usage
			d =	new Duration(
					2.0 * this.meanTimeAtHigh * this.rg.nextBeta(1.75, 1.75),
					this.getSimulatedTimeUnit()) ;
			Time t = this.getCurrentStateTime().add(d) ;
			this.scheduleEvent(this.createSwitchOffEvent(t)) ;
		}
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return null ;
	}

	/**
	 * create a switch on event, implementing a factory pattern.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		time of occurrence of the event.
	 * @return		the new switch on event.
	 */
	protected ES_EventI	createSwitchOnEvent(Time t)
	{
		return new SwitchOnSIL(t) ;
	}

	/**
	 * create a switch off event, implementing a factory pattern.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		time of occurrence of the event.
	 * @return		the new switch off event.
	 */
	protected ES_EventI createSwitchOffEvent(Time t)
	{
		return new SwitchOffSIL(t) ;
	}

	/**
	 * create a set high event, implementing a factory pattern.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		time of occurrence of the event.
	 * @return		the new set high event.
	 */
	protected ES_EventI createSetHighEvent(Time t)
	{
		return new SetHighSIL(t) ;
	}

	/**
	 * create a set low event, implementing a factory pattern.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		time of occurrence of the event.
	 * @return		the new set low event.
	 */
	protected ES_EventI	createSetLowEvent(Time t)
	{
		return new SetLowSIL(t) ;
	}
}
// -----------------------------------------------------------------------------
