package fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models;

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

import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.AbstractHairDryerEvent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetHigh;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetLow;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOff;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOn;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerModel</code> implements a DEVS MIL
 * simulation model of a hair dryer providing the current intensity of
 * electricity consumption as a continuous variable.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The hair dryer can of course be switch on and off as well as set to a low
 * temperature or a high temperature mode. In the low temperature mode, the
 * power consumption is 660 watts while in the high mode it is 1100 watts.
 * The appliance is assumed to use a 220 volts tension. The model compute
 * the intensity of the current over time in amperes.
 * </p>
 * <p>
 * The hair dryer model is commanded through four external events:
 * <code>SwitchOn</code>, <code>SwitchOff</code>, <code>SetLow</code> and
 * <code>SetHigh</code> to make it go from one state to the other. When the
 * hair dryer is switched on, it is put in the low mode.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true	// TODO
 * </pre>
 * 
 * <p>Created on : 2019-10-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOn.class,
								 SwitchOff.class,
								 SetLow.class,
								 SetHigh.class},
					 exported = {ConsumptionIntensity.class})
// -----------------------------------------------------------------------------
public class			HairDryerMILModel
extends		AtomicHIOAwithEquations
implements	SGMILModelImplementationI
{
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>State</code> describes the discrete states or
	 * modes of the hair dryer.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * The hair dryer can be <code>OFF</code> or on, and then it is either in
	 * <code>LOW</code> mode (less hot and less consuming) or in
	 * <code>HIGH</code> mode (hotter and more consuming).
	 * 
	 * <p>Created on : 2019-10-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum State {
		OFF,
		/** low mode is less hot and less consuming.						*/
		LOW,			
		/** high mode is hotter and more consuming.							*/
		HIGH
	}

	/**
	 * The class <code>HairDryerReport</code> implements the simulation
	 * report of the hair dryer model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2019-10-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		HairDryerReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L;
		
		public			HairDryerReport(String modelURI)
		{
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "HairDryerReport(" + this.getModelURI() + ")" ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L ;
	/** URI used to create instances of the model; assumes a singleton,
	 *  otherwise a different URI must be given to each instance.			*/
	public static final String		URI = HairDryerMILModel.class.getName() ;
	public static final String		COMPONENT_HOLDER_REF_PARAM_NAME =
											"hair dryer component reference" ;

	public static final String		SERIES = "intensity" ;

	/** energy consumption (in Watts) of the hair dryer in LOW mode.		*/
	public static final double		LOW_MODE_CONSUMPTION = 660.0 ; // Watts
	/** energy consumption (in Watts) of the hair dryer in HIGH mode.		*/
	public static final double		HIGH_MODE_CONSUMPTION = 1100.0 ; // Watts
	/** nominal tension (in Volts) of the hair dryer.						*/
	public static final double		TENSION = 220.0 ; // Volts

	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity =
											new Value<Double>(this, 0.0, 0) ;
	/** current state (OFF, LOW, HIGH) of the hair dryer.					*/
	protected State					currentState ;
	/** true when the electricity consumption of the dryer has changed.		*/
	protected boolean				consumptionHasChanged ;

	/** plotter for the intensity level over time.							*/
	protected XYPlotter				intensityPlotter ;

	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentAccessI componentRef ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	simulatedTimeUnit != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do.</i>
	 */
	public				HairDryerMILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger()) ;
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void		finalize() throws Throwable
	{
		if (this.intensityPlotter != null) {
			this.intensityPlotter.dispose() ;
		}
		super.finalize();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

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
			(EmbeddingComponentAccessI)
							simParams.get(COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// the hair dryer starts in mode OFF
		this.currentState = State.OFF ;
		this.consumptionHasChanged = false ;

		// creation of a plotter to show the evolution of the intensity over
		// time during the simulation.
		PlotterDescription pd =
				new PlotterDescription(
						"Hair dryer intensity",
						"Time (sec)",
						"Intensity (Amp)",
						100,
						0,
						600,
						400) ;
		this.intensityPlotter = new XYPlotter(pd) ;
		this.intensityPlotter.createSeries(SERIES) ;
		// initialisation of the intensity plotter 
		this.intensityPlotter.initialise() ;
		// show the plotter on the screen
		this.intensityPlotter.showPlotter() ;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		super.initialiseState(initialTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		// as the hair dryer starts in mode OFF, its power consumption is 0
		this.currentIntensity.v = 0.0 ;

		// first data in the plotter to start the plot.
		this.intensityPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getIntensity());

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.componentRef == null) {
			// the model has no internal event, however, its state will evolve
			// upon reception of external events.
			if (this.consumptionHasChanged) {
				this.toggleConsumptionHasChanged() ;
				return Duration.zero(this.getSimulatedTimeUnit()) ;
			} else {
				return Duration.INFINITY ;
			}
		} else {
			// This is to test the embedding component access facility.
			return new Duration(10.0, TimeUnit.SECONDS) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		this.logMessage("emitting new consumption intensity level: " +
										this.currentIntensity.v + " amp.") ;
		ArrayList<EventI> ret = new ArrayList<EventI>() ;
		Time t = this.getCurrentStateTime().add(getNextTimeAdvance()) ;
		try {
			ret.add(new ConsumptionIntensity(t,
											 this.getURI(),
											 this.currentIntensity.v)) ;
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
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			try {
				this.logMessage("component state = " +
								this.componentRef.
									getEmbeddingComponentStateValue("state")) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 1") ;
		}

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset() ;
		// when this method is called, there is at least one external event,
		// and for the hair dryer model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1 ;

		Event ce = (Event) currentEvents.get(0) ;
		assert	ce instanceof AbstractHairDryerEvent ;
		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 2 "
										+ ce.getClass().getCanonicalName()) ;
		}

		// the plot is piecewise constant; this data will close the currently
		// open piece
		this.intensityPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getIntensity());

		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 3 "
															+ this.getState()) ;
		}

		// execute the current external event on this model, changing its state
		// and intensity level
		ce.executeOn(this) ;

		if (this.hasDebugLevel(1)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 4 "
															+ this.getState()) ;
		}

		// add a new data on the plotter; this data will open a new piece
		this.intensityPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getIntensity());

		super.userDefinedExternalTransition(elapsedTime) ;
		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 5") ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.intensityPlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.getIntensity()) ;

		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new HairDryerReport(this.getURI()) ;
	}

	// -------------------------------------------------------------------------
	// Model-specific methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI#disposePlotters()
	 */
	@Override
	public void			disposePlotters() throws Exception
	{
		if (this.intensityPlotter != null) {
			this.intensityPlotter.dispose() ;
			this.intensityPlotter = null ;
		}
	}

	/**
	 * set the state of the hair dryer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	s != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void			setState(State s)
	{
		this.currentState = s ;
		switch (s)
		{
			case OFF : this.currentIntensity.v = 0.0 ; break ;
			case LOW :
				this.currentIntensity.v = LOW_MODE_CONSUMPTION/TENSION ;
				break ;
			case HIGH :
				this.currentIntensity.v = HIGH_MODE_CONSUMPTION/TENSION ;
		}
	}

	/**
	 * return the state of the hair dryer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return	the state of the hair dryer.
	 */
	public State		getState()
	{
		return this.currentState ;
	}

	public void			toggleConsumptionHasChanged()
	{
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false ;
		} else {
			this.consumptionHasChanged = true ;
		}
	}

	/**
	 * return the current intensity of electricity consumption in amperes.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	{@code ret >= 0.0 and ret <= 1200.0/220.0}
	 * </pre>
	 *
	 * @return	the current intensity of electricity consumption in amperes.
	 */
	public double		getIntensity()
	{
		return this.currentIntensity.v ;
	}
}
// -----------------------------------------------------------------------------
