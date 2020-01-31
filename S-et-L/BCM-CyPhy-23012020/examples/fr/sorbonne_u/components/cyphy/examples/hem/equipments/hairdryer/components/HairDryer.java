package fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components;

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

import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.EquipmentDirectory;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.interfaces.HairDryerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.ports.HairDryerInboundPort;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.interfaces.EnergyManagerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;

//------------------------------------------------------------------------------
/**
 * The class <code>HairDryer</code> implements a hair dryer component that will
 * hold the hair dryer simulation model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//------------------------------------------------------------------------------
@OfferedInterfaces(offered = {HairDryerCI.class})
@RequiredInterfaces(required = {EnergyManagerCI.class})
//------------------------------------------------------------------------------
public class			HairDryer
extends		AbstractCyPhyComponent
implements	HairDryerImplementationI,
			EmbeddingComponentAccessI
{
	// -------------------------------------------------------------------------
	// Inner interfaces, classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>HairDryerMode</code> describes the operation
	 * modes of the hair dryer.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * The hair dryer can be <code>OFF</code> or on, and then it is either in
	 * <code>LOW</code> mode (warm and slow) or in <code>HIGH</code> mode (hot
	 * and fast).
	 * 
	 * <p>Created on : 2020-01-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum HairDryerMode {
		OFF,
		/** low mode is just warm and the fan is slower.					*/
		LOW,			
		/** high mode is hot and the fan turns faster.						*/
		HIGH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the URI of the simulation architecture used when performing a stand
	 *  alone SIL simlation, typically to perform unit testing.				*/
	public static final String	SIL_STAND_ALONE =
									"hair dryer SIL stand alone architecture" ;
	/** the simulation plug-in holding the simulation models.				*/
	protected HairDryerSimulatorPlugin					asp ;

	/** the current functioning mode of the hair dryer.						*/
	protected HairDryerMode								currentMode ;
	/** the inbound port offering the component services.					*/
	protected HairDryerInboundPort						servicesInboundPort ;
	/** the outbound port used to connect the hair dryer to the energy
	 *  manager.															*/
	protected AbstractOutboundPort						portToEnergyManager ;
	/** the simulation mode used for the current execution.					*/
	protected SimulationArchitectures.SimulationMode	simMode ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer for an execution using the given simulation
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simArchitectureURI	the URI of the simulaiton architecture used in the current execution.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			HairDryer(String simArchitectureURI) throws Exception
	{
		// 3 threads to be able to execute tasks and requests while executing
		// the DEVS simulation.
		super(3, 0) ;

		assert	simArchitectureURI != null ;

		this.initialise(simArchitectureURI) ;
	}

	/**
	 * create a hair dryer for an execution using the given simulation
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			HairDryer(
		String reflectionInboundPortURI,
		String simArchitectureURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;

		assert	simArchitectureURI != null ;

		this.initialise(simArchitectureURI) ;
	}

	/**
	 * initialise the hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(String simArchitectureURI) throws Exception
	{
		if (!simArchitectureURI.equals(SimulationArchitectures.NONE)) {
			String modelURI = null ;
			if (simArchitectureURI.equals(SimulationArchitectures.MIL)) {
				modelURI = HairDryerMILCoupledModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.MIL ;
			} else if (simArchitectureURI.equals(HairDryer.SIL_STAND_ALONE)) {
				modelURI = HairDryerSILCoupledModel.URI ;
				this.simMode =
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE ;
			} else if (simArchitectureURI.equals(SimulationArchitectures.SIL)) {
				modelURI = HairDryerSILCoupledModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.SIL ;
			} else {
				throw new Exception("unknown simulation architecture URI: " +
														simArchitectureURI) ;
			}
			// The coupled model has been made able to create the simulation
			// architecture description.
			Architecture localArchitecture =
										this.createLocalArchitecture(modelURI) ;
			// Create the appropriate DEVS simulation plug-in.
			this.asp = new HairDryerSimulatorPlugin() ;
			// Set the URI of the plug-in, using the URI of its associated
			// simulation model.
			this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
			// Set the simulation architecture.
			this.asp.setSimulationArchitecture(localArchitecture) ;
			// Install the plug-in on the component, starting its own life-cycle.
			this.installPlugin(this.asp) ;
		} else {
			this.simMode = null ;
		}

		this.servicesInboundPort = new HairDryerInboundPort(this) ;
		this.servicesInboundPort.publishPort() ;

		// Toggle logging on to get a log on the screen.
		this.tracer.setTitle("Hair dryer") ;
		this.tracer.setRelativePosition(1, 1) ;
		this.toggleTracing() ;	
	}

	// -------------------------------------------------------------------------
	// Local methods
	// -------------------------------------------------------------------------

	/**
	 * return a reference to a port requiring the energy manager component
	 * interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	a reference to a port requiring the energy manager component interface.
	 */
	protected EnergyManagerCI	getEnergyManagerRef()
	{
		return (EnergyManagerCI) this.portToEnergyManager ;
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryerImplementationI#getMode()
	 */
	@Override
	public HairDryerMode getMode() throws Exception
	{
		return this.currentMode ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryerImplementationI#turnOn()
	 */
	@Override
	public void			turnOn() throws Exception
	{
		assert	this.getMode() == HairDryerMode.OFF ;

		this.traceMessage("hair dryer turned on.\n");
		this.currentMode = HairDryerMode.LOW ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryerImplementationI#turnOff()
	 */
	@Override
	public void			turnOff() throws Exception
	{
		assert	this.getMode() != HairDryerMode.OFF ;

		this.traceMessage("hair dryer turned off.\n");
		this.currentMode = HairDryerMode.OFF ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryerImplementationI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception
	{
		assert	this.getMode() == HairDryerMode.LOW ;

		this.traceMessage("hair dryer set to high mode.\n");
		this.currentMode = HairDryerMode.HIGH ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryerImplementationI#setLow()
	 */
	@Override
	public void			setLow() throws Exception
	{
		assert	this.getMode() == HairDryerMode.HIGH ;

		this.traceMessage("hair dryer set to low mode.\n");
		this.currentMode = HairDryerMode.LOW ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		this.currentMode = HairDryerMode.OFF ;
		try {
			if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
				this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
			{
				this.portToEnergyManager =
					EquipmentDirectory.
							createOutboundPort(EnergyManagerCI.class, this) ;
				this.portToEnergyManager.publishPort() ;
				this.doPortConnection(
					this.portToEnergyManager.getPortURI(),
					EquipmentDirectory.ENERGY_MANAGER_INBOUNDPORT_URI,
					EquipmentDirectory.connectorClassName(
													EnergyManagerCI.class)) ;
			} else {
				// no connections to other components
			}
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (this.simMode == SimulationArchitectures.SimulationMode.SIL ||
				   										this.simMode == null) {
			this.getEnergyManagerRef().connect(
									HairDryerCI.class,
									this.servicesInboundPort.getPortURI()) ;
		} else if (this.simMode ==
					SimulationArchitectures.SimulationMode.SIL_STAND_ALONE) {
			this.silStandAloneSimulation() ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		
		if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
			this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
		{
			this.doPortDisconnection(this.portToEnergyManager.getPortURI()) ;
		}

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.servicesInboundPort.unpublishPort() ;
			if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
				this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
			{
				this.portToEnergyManager.unpublishPort() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.servicesInboundPort.unpublishPort() ;
			if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
				this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
			{
				this.portToEnergyManager.unpublishPort() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

	// -------------------------------------------------------------------------
	// Methods for simulation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture	createLocalArchitecture(String modelURI)
	throws Exception
	{
		assert	modelURI != null ;

		if (modelURI.equals(HairDryerMILCoupledModel.URI)) {
			return HairDryerMILCoupledModel.buildArchitecture() ;
		} else if (modelURI.equals(HairDryerSILCoupledModel.URI)) {
			return HairDryerSILCoupledModel.buildArchitecture() ;
		} else {
			throw new Exception("unknown model URI: " + modelURI + ".") ;
		}
	}

	protected void		silStandAloneSimulation() throws Exception
	{
		// Start the simulation.
		this.runTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((HairDryer)this.getTaskOwner()).
												silStandAloneSimulationRun() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}) ;
	}

	/**
	 * running the SIL model for the hair dryer for 500 seconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		silStandAloneSimulationRun() throws Exception
	{
		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		this.asp.setSimulationRunParameters(simParams) ;
		asp.doStandAloneSimulation(0.0, 500.0) ;
		Thread.sleep(5000) ;
		asp.finaliseSimulation() ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object		getEmbeddingComponentStateValue(String name)
	throws Exception
	{
		// As there is only one value, don't care about the name.
		return this.currentMode ; // just an example...
		// With this facility, the state of the hair dryer can be set by the
		// component part (e.g., the controller) rather than by the user model
		// included in the hair dryer model in a development process going from
		// a pure MIL simulation to a SIL simulation.
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#setEmbeddingComponentStateValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void			setEmbeddingComponentStateValue(
		String name,
		Object value
		) throws Exception
	{
		// As there is only one value, don't care about the name, but the
		// value must a hair dryer functioning mode.
		assert	value instanceof HairDryerMode ;
		// set the new mode.
		this.currentMode = (HairDryerMode) value ;
	}
}
//------------------------------------------------------------------------------
