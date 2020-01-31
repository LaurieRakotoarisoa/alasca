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

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Map;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer.HairDryerMode;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel.HairDryerReport;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel.State;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerSILModel</code> implements a DEVS SIL
 * simulation model of a hair dryer providing the current intensity of
 * electricity consumption as a continuous variable.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is implemented to work with its embedding component. To keep
 * track of the current operating mode of the hair dryer, it calls the
 * component to get the value. This way of polling the component from the
 * model is imposed by the current simulation algorithm which makes difficult
 * to generate simulation events from the outside of the simulation code (e.g.,
 * the component code). In a real-time simulation algorithm, this would be much
 * simpler, hence making it possible to avoid the polling and its cost in
 * computation time.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-01-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(exported = {ConsumptionIntensity.class})
// -----------------------------------------------------------------------------
public class			HairDryerSILModel
extends		AtomicHIOAwithEquations
implements	SGMILModelImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** URI used to create instances of the model; assumes a singleton,
	 *  otherwise a different URI must be given to each instance.			*/
	public static final String	URI = HairDryerSILModel.class.getName() ;
	public static final double	PEEK_DELAY = 0.1 ; // in seconds

	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity =
											new Value<Double>(this, 0.0, 0) ;

	/** state before the last reading (OFF, LOW, HIGH) of the hair dryer.	*/
	protected HairDryerMode			lastMode ;
	/** current state (OFF, LOW, HIGH) of the hair dryer.					*/
	protected State					currentState ;
	/** true when the electricity consumption of the dryer has changed.		*/
	protected boolean				consumptionHasChanged ;

	/** plotter for the intensity level over time.							*/
	protected XYPlotter				intensityPlotter ;

	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected HairDryer 			componentRef ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer SIL model instance.
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
	public				HairDryerSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

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
			(HairDryer) simParams.get(
							HairDryerMILModel.COMPONENT_HOLDER_REF_PARAM_NAME) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// the hair dryer starts in mode OFF
		this.currentState = State.OFF ;
		this.lastMode = HairDryerMode.OFF ;
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
		this.intensityPlotter.createSeries(HairDryerMILModel.SERIES) ;
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
				HairDryerMILModel.SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.getIntensity());

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.consumptionHasChanged) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return new Duration(PEEK_DELAY, this.getSimulatedTimeUnit()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (this.consumptionHasChanged) {
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
			this.consumptionHasChanged = false ;
			return ret ;
		} else {
			return null ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		try {
			// the plot is piecewise constant; this data will close the currently
			// open piece
			this.intensityPlotter.addData(
					HairDryerMILModel.SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.getIntensity());

			assert	this.componentRef != null ;
			HairDryerMode m =
				(HairDryerMode) this.componentRef.
										getEmbeddingComponentStateValue("") ;
			if (m != this.lastMode) {
				switch(m)
				{
					case OFF : this.setState(State.OFF) ; break ;
					case LOW : this.setState(State.LOW) ; break ;
					case HIGH : this.setState(State.HIGH) ;
				}
				this.consumptionHasChanged = true ;
				this.lastMode = m ;
			}

			// add a new data on the plotter; this data will open a new piece
			this.intensityPlotter.addData(
					HairDryerMILModel.SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.getIntensity());

		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		// No external imported events.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.intensityPlotter.addData(
				HairDryerMILModel.SERIES,
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
				this.currentIntensity.v =
						HairDryerMILModel.LOW_MODE_CONSUMPTION/
												HairDryerMILModel.TENSION ;
				break ;
			case HIGH :
				this.currentIntensity.v =
						HairDryerMILModel.HIGH_MODE_CONSUMPTION/
												HairDryerMILModel.TENSION ;
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
