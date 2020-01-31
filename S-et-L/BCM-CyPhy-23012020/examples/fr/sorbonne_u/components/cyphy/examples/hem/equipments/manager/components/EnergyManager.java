package fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.components;

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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.EquipmentDirectory;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer.HairDryerMode;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.interfaces.HairDryerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.interfaces.EnergyManagerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.mil.models.EnergyManagerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.ports.EnergyManagerInboundPort;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.sil.models.EnergyManagerSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;

// -----------------------------------------------------------------------------
/**
 * The class <code>EnergyManager</code> implements an energy manager as a
 * component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-01-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//------------------------------------------------------------------------------
@RequiredInterfaces(required = {ReflectionI.class,
								ElectricMeterCI.class,
								HairDryerCI.class})
@OfferedInterfaces(offered = {EnergyManagerCI.class})
//------------------------------------------------------------------------------
public class			EnergyManager
extends		AbstractCyPhyComponent
implements	EnergyManagerImplementationI,
			EnergyManagerComponentAccessI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** set to true when testing teh component without simulation and to false
	 *  when simulating or for a hardware-in-the-loop simulation or a normally
	 *  functioning component (not really implemented yet).					*/
	public static final boolean			TESTING = false ;
	/** period at which the control task is activated.						*/
	public static final double			CONTROL_PERIOD = 1.0 ;
	/** energy consumption threshold triggering a decision.					*/
	public static final double			THRESHOLD = 4.0 ;
	/** URI of the reflection inbound port of the electric meter for initial
	 *  connection.															*/
	protected final String				electricMeterReflectionInboundPortURI ;
	/** outbound port used to connect to the electric meter.				*/
	protected AbstractOutboundPort					electricMeterOutboundPort ;
	/** map from URI to the outbound port connecting the manager to the
	 *  equipments it controls.												*/
	protected HashMap<String,AbstractOutboundPort>		equipments ;
	/** the inbound port offering the energy manager component interface
	 *  used by the equipments to connect themselves to the manager.	 	*/
	protected EnergyManagerInboundPort					emInboundPort ;

	/** the simulator plug-in holding the simulation model attached to the
	 *  component.															*/
	protected AtomicSimulatorPlugin						asp ;
	/** the simulation mode of the current execution of the component.		*/
	protected SimulationArchitectures.SimulationMode	simMode ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	electricMeterReflectionInboundPortURI != null
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterReflectionInboundPortURI	URI of the reflection inbound port of the electric meter for initial connection.
	 * @param simArchitectureURI					the URI of the simulation architecture to be created and run.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			EnergyManager(
		String electricMeterReflectionInboundPortURI,
		String simArchitectureURI
		) throws Exception
	{
		super(2, 1) ;

		assert	electricMeterReflectionInboundPortURI != null ;
		assert	simArchitectureURI != null ;

		this.electricMeterReflectionInboundPortURI =
										electricMeterReflectionInboundPortURI ;
		this.initialise(simArchitectureURI) ;
	}

	/**
	 * create an energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * pre	electricMeterReflectionInboundPortURI != null
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param electricMeterReflectionInboundPortURI	URI of the reflection inbound port of the electric meter for initial connection.
	 * @param simArchitectureURI					the URI of the simulation architecture to be created and run.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			EnergyManager(
		String reflectionInboundPortURI,
		String electricMeterReflectionInboundPortURI,
		String simArchitectureURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 1) ;
		this.electricMeterReflectionInboundPortURI =
										electricMeterReflectionInboundPortURI ;
		this.initialise(simArchitectureURI) ;
	}

	/**
	 * initialise the energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simArchitectureURI	the URI of the simulation architecture to be created and run.
	 * @throws Exception			<i>to do</i>.
	 */
	protected void		initialise(String simArchitectureURI) throws Exception
	{
		if (!simArchitectureURI.equals(SimulationArchitectures.NONE)) {
			String modelURI = null ;
			if (simArchitectureURI.equals(SimulationArchitectures.MIL)) {
				modelURI = EnergyManagerMILModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.MIL ;
			} else if (simArchitectureURI.equals(SimulationArchitectures.SIL)) {
				modelURI = EnergyManagerSILModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.SIL ;
			}
			Architecture localArchitecture =
									this.createLocalArchitecture(modelURI) ;
			// a good place to capture the reference to the component and
			// place a method that will intercept the initialisation of the
			// model before each runs to add the appropriate value.
			EnergyManagerComponentAccessI ref = this ;
			this.asp = new AtomicSimulatorPlugin() {
							private static final long serialVersionUID = 1L ;

							@Override
							public void setSimulationRunParameters(
								Map<String, Object> simParams
								) throws Exception
							{
								simParams.put(
									EnergyManagerSILModel.
											COMPONENT_HOLDER_REF_PARAM_NAME,
									ref) ;
								super.setSimulationRunParameters(simParams) ;
								simParams.remove(
										EnergyManagerSILModel.
											COMPONENT_HOLDER_REF_PARAM_NAME) ;
							}
						} ;
			this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
			this.asp.setSimulationArchitecture(localArchitecture) ;
			this.installPlugin(this.asp) ;
		} else {
			this.simMode = null ;
		}

		this.equipments = new HashMap<String,AbstractOutboundPort>() ;

		this.emInboundPort =
			new EnergyManagerInboundPort(
					EquipmentDirectory.ENERGY_MANAGER_INBOUNDPORT_URI,
					this) ;
		this.emInboundPort.publishPort() ;

		this.electricMeterOutboundPort =
			EquipmentDirectory.createOutboundPort(ElectricMeterCI.class, this) ;
		this.electricMeterOutboundPort.publishPort() ;

		// Toggle logging on to get a log on the screen.
		this.tracer.setTitle("Energy manager") ;
		this.tracer.setRelativePosition(1, 0) ;
		this.toggleTracing() ;	
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * get a reference on the port requiring the <code>ElectricMeterCI</code>
	 * component interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the reference to the outbound port connected to the electric meter.
	 */
	protected ElectricMeterCI	getElectricMeterRef()
	{
		return (ElectricMeterCI) this.electricMeterOutboundPort ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.components.EnergyManagerComponentAccessI#getControlPeriod()
	 */
	@Override
	public double		getControlPeriod()
	{
		return CONTROL_PERIOD ;
	}

	protected HairDryerCI	findHairDryer()
	{
		for (AbstractOutboundPort p : this.equipments.values()) {
			if (p instanceof HairDryerCI) {
				return (HairDryerCI) p ;
			}
		}
		return null ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.components.EnergyManagerComponentAccessI#controlTask(double)
	 */
	@Override
	public void			controlTask(double simulatedTime) throws Exception
	{
		double currentIntensity = 0.0 ;
		if (this.getElectricMeterRef() != null) {
			currentIntensity = this.getElectricMeterRef().readCurrentLevel() ;
		}
		if (simulatedTime >= 0.0) {
			this.traceMessage("control task executing at " + simulatedTime +
							  "with current intensity = " + currentIntensity +
							  "\n") ;
		}

		if (this.getElectricMeterRef() != null) {
			if (currentIntensity > THRESHOLD) {
				HairDryerCI oport = this.findHairDryer() ;
				if (oport != null) {
					this.traceMessage("energy manager reduces the consumption"
													+ " of the hair dryer.\n") ;
					HairDryerMode m = oport.getMode() ;
					this.traceMessage("hair dryer is in mode: " + m + ".\n") ;
					switch (m)
					{
						case HIGH : oport.setLow() ; break ;
						case LOW :
						case OFF :
					}
					m = oport.getMode() ;
					this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
				}

			}
		}
	}
	protected void		reduceHairDryerConsumption() throws Exception
	{
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.components.EnergyManagerImplementationI#connect(java.lang.Class, java.lang.String)
	 */
	@Override
	public void			connect(
		Class<?> inter,
		String inboundPortURI
		) throws Exception
	{
		this.traceMessage("connecting " + inter.getCanonicalName() + "\n") ;
		AbstractOutboundPort p =
						EquipmentDirectory.createOutboundPort(inter, this) ;
		p.publishPort() ;
		this.doPortConnection(p.getPortURI(),
							  inboundPortURI,
							  EquipmentDirectory.connectorClassName(inter)) ;
		this.equipments.put(inboundPortURI, p) ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (this.simMode == SimulationArchitectures.SimulationMode.SIL ||
														this.simMode == null) {
			ReflectionOutboundPort rop = new ReflectionOutboundPort(this) ;
			rop.publishPort() ;
			this.doPortConnection(rop.getPortURI(),
							  electricMeterReflectionInboundPortURI,
							  ReflectionConnector.class.getCanonicalName()) ;
			String[] uris =
				rop.findInboundPortURIsFromInterface(ElectricMeterCI.class) ;
			assert	uris != null && uris.length == 1 && uris[0] != null ;
			this.doPortDisconnection(rop.getPortURI()) ;
			rop.unpublishPort() ;
			rop.destroyPort() ;
			this.doPortConnection(
				this.electricMeterOutboundPort.getPortURI(),
				uris[0],
				EquipmentDirectory.connectorClassName(ElectricMeterCI.class)) ;

			if (this.simMode == SimulationArchitectures.SimulationMode.MIL ||
				this.simMode == SimulationArchitectures.SimulationMode.SIL)
			{
				// do nothing; for SIL, the control task will be triggered by
				// the simulation model
			} else if (TESTING) {
				// a simple test, without simulation
				this.firstDebuggingTest() ;
			} else {
				// the real control program, can't be used actually
				long period = (long)(CONTROL_PERIOD*1000.0) ;
				if (period <= 0) {
					period = 100L ;
				}
				this.scheduleTaskAtFixedRate(
						new AbstractComponent.AbstractTask() {
							@Override
							public void run() {
								try {
									((EnergyManager)this.getTaskOwner()).
															controlTask(-1.0) ;
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						},
						period,
						period,
						TimeUnit.SECONDS) ;
			}
		}
	}

	/**
	 * a simple integration test calling every possible service from each
	 * component.
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
	protected void		firstDebuggingTest() throws Exception
	{
		this.traceMessage("manager executing\n") ;
		while (this.equipments.isEmpty()) {
			this.traceMessage("manager waiting connections...\n") ;
			Thread.sleep(1000L) ;
		}
		this.traceMessage("manager having connections.\n") ;

		HairDryerCI oport = this.findHairDryer() ;
		this.traceMessage("manager found hair dryer.\n") ;
		oport.turnOn() ;
		HairDryerMode m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		double level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.setHigh() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.setLow() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.turnOff() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.turnOn() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.setHigh() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.setLow() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
		oport.turnOff() ;
		m = oport.getMode() ;
		this.traceMessage("hair dryer is now in mode: " + m + ".\n") ;
		level = this.getElectricMeterRef().readCurrentLevel() ;
		this.traceMessage("electric meter level: " + level + "\n") ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.simMode == SimulationArchitectures.SimulationMode.SIL ||
														this.simMode == null) {
			this.doPortDisconnection(
						this.electricMeterOutboundPort.getPortURI()) ;
		}
		for (AbstractOutboundPort p : this.equipments.values()) {
			this.doPortDisconnection(p.getPortURI()) ;
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
			this.emInboundPort.unpublishPort() ;
			this.electricMeterOutboundPort.unpublishPort() ;
			for (AbstractOutboundPort p : this.equipments.values()) {
				p.unpublishPort() ;
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
			this.emInboundPort.unpublishPort() ;
			this.electricMeterOutboundPort.unpublishPort() ;
			for (AbstractOutboundPort p : this.equipments.values()) {
				p.unpublishPort() ;
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
		if (modelURI.equals(EnergyManagerMILModel.URI)) {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;

			atomicModelDescriptors.put(
					EnergyManagerMILModel.URI,
					AtomicModelDescriptor.create(
							EnergyManagerMILModel.class,
							EnergyManagerMILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			Architecture localArchitecture =
					new Architecture(
							EnergyManagerMILModel.URI,
							atomicModelDescriptors,
							new HashMap<>(),
							TimeUnit.SECONDS) ;
			return localArchitecture ;
		} else if (modelURI.equals(EnergyManagerSILModel.URI)) {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;

			atomicModelDescriptors.put(
					EnergyManagerSILModel.URI,
					AtomicModelDescriptor.create(
							EnergyManagerSILModel.class,
							EnergyManagerSILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			Architecture localArchitecture =
					new Architecture(
							EnergyManagerSILModel.URI,
							atomicModelDescriptors,
							new HashMap<>(),
							TimeUnit.SECONDS) ;
			return localArchitecture ;

		} else {
			return null ;
		}
	}
}
// -----------------------------------------------------------------------------
