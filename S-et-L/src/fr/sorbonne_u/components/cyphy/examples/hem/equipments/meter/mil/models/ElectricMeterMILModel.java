package fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.models;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>EleectricMeterMILModel</code> implements a simple electricity
 * meter simulator for MIL simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-01-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {ConsumptionIntensity.class},
					 exported = {ConsumptionIntensity.class})
// -----------------------------------------------------------------------------
public class			ElectricMeterMILModel
extends		AtomicModel
implements	SGMILModelImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L ;
	/** URI used to create instances of the model; assumes a singleton,
	 *  otherwise a different URI must be given to each instance.			*/
	public static final String		URI = ElectricMeterMILModel.class.getName() ;

	/** the standard delay between two consumption level pushes to the
	 *  energy manager.														*/
	public static final double		PUSH_DELAY = 1.0 ; // in seconds
	/** last received energy consumption level from each of the connected
	 *  equipment.															*/
	protected Map<String,Double>	lastTransmittedIntensity ;
	/** current total energy consumption level.								*/
	protected double				currentIntensity ;
	/** delay until the next push to the energy manager.					*/
	protected Duration				nextDelay ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				ElectricMeterMILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;
		assert	simulatedTimeUnit == TimeUnit.SECONDS ;

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.nextDelay =
			new Duration(PUSH_DELAY, this.getSimulatedTimeUnit()) ;

		super.initialiseState(initialTime) ;

		this.lastTransmittedIntensity = new HashMap<String,Double>() ;
		this.currentIntensity = 0.0 ;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.nextDelay ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		ArrayList<EventI> ret = new ArrayList<EventI>(1) ;
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
		try {
			ret.add(new ConsumptionIntensity(t,
											 this.getURI(),
											 this.currentIntensity)) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;

		this.logMessage("intensity = " + this.currentIntensity + " at " +
						this.getCurrentStateTime().getSimulatedTime()) ;
		this.nextDelay = new Duration(PUSH_DELAY, this.getSimulatedTimeUnit()) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		ArrayList<EventI> current = this.getStoredEventAndReset() ;
		// in case several external consumption events are emitted at the same
		// simulation time.
		for (EventI e : current) {
			assert	e instanceof ConsumptionIntensity ;
			ConsumptionIntensity intensity = (ConsumptionIntensity) e ;
			double last = 0.0 ;
			if (this.lastTransmittedIntensity.containsKey(
											intensity.getEmittingMoldeURI())) {
				last = this.lastTransmittedIntensity.
										get(intensity.getEmittingMoldeURI()) ;
			}
			this.currentIntensity += (intensity.getIntensity() - last) ;
			this.logMessage(
					"last = " + last + " new = " + intensity.getIntensity() +
					" current = " + this.currentIntensity +
					" at " + this.getCurrentStateTime().getSimulatedTime()) ;
			this.lastTransmittedIntensity.put(intensity.getEmittingMoldeURI(),
											  intensity.getIntensity()) ;
		}
		this.nextDelay =
			new Duration(this.nextDelay.getSimulatedDuration() -
											elapsedTime.getSimulatedDuration(),
						 this.getSimulatedTimeUnit()) ;
	}
}
// -----------------------------------------------------------------------------
