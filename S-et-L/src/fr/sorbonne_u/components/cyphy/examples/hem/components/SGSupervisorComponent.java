package fr.sorbonne_u.components.cyphy.examples.hem.components;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetHigh;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetLow;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOff;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOn;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.mil.models.EnergyManagerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.sil.models.EnergyManagerSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.models.ElectricMeterMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.sil.models.ElectricMeterSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SGCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;

// -----------------------------------------------------------------------------
/**
 * The class <code>SGSupervisorComponent</code> implements a supervisor for
 * simulations of the household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The component creates a simulation architecture and then executes simulation
 * runs. Two architectures are defined:
 * </p>
 * <ol>
 * <li>when the constructors are passed the architecture URI
 *   <code>SimulationArchitectures.MIL</code>, they create a MIL simulation
 *   architecture;</li>
 * <li>when the constructors are passed the architecture URI
 *   <code>SimulationArchitectures.SIL</code>, they create a SIL simulation
 *   architecture.</li>
 * </ol>
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
public class			SGSupervisorComponent
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the supervisor plug-in attached to this component.					*/
	protected SupervisorPlugin		sp ;
	/** maps from URIs of models to URIs of the reflection inbound ports
	 *  of the components that hold them.									*/
	protected Map<String,String>	modelURIs2componentURIs ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor component with a self-generated reflection inbound
	 * port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simArchitectureURI != null
	 * pre	modelURIs2componentURIs != null
	 * pre	{@code modelURIs2componentURIs.size() >= 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @param modelURIs2componentURIs	map from URIs of the simulation models and the URI of the reflection inbound port of the component holding them.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			SGSupervisorComponent(
		String simArchitectureURI,
		Map<String,String> modelURIs2componentURIs
		) throws Exception
	{
		super(2, 0) ;

		assert	simArchitectureURI != null ;
		assert	modelURIs2componentURIs != null ;
		assert	modelURIs2componentURIs.size() >= 1 ;

		this.initialise(simArchitectureURI, modelURIs2componentURIs) ;
	}

	/**
	 * create a supervisor component with a given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * pre	simArchitectureURI != null
	 * pre	modelURIs2componentURIs != null
	 * pre	{@code modelURIs2componentURIs.size() >= 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @param modelURIs2componentURIs	map from URIs of the simulation models and the URI of the reflection inbound port of the component holding them.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			SGSupervisorComponent(
		String reflectionInboundPortURI,
		String simArchitectureURI,
		Map<String,String> modelURIs2componentURIs
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0) ;

		assert	simArchitectureURI != null ;
		assert	modelURIs2componentURIs != null ;
		assert	modelURIs2componentURIs.size() >= 1 ;

		this.initialise(simArchitectureURI, modelURIs2componentURIs) ;
	}

	/**
	 * initialise the supervisor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simArchitectureURI != null
	 * pre	modelURIs2componentURIs != null
	 * pre	{@code modelURIs2componentURIs.size() >= 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @param modelURIs2componentURIs	map from URIs of the simulation models and the URI of the reflection inbound port of the component holding them.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		String simArchitectureURI,
		Map<String,String> modelURIs2componentURIs
		) throws Exception
	{
		this.modelURIs2componentURIs = modelURIs2componentURIs ;

		this.tracer.setTitle("Supervisor component") ;
		this.tracer.setRelativePosition(0, 4) ;
		this.toggleTracing() ;

		if (simArchitectureURI.equals(SimulationArchitectures.MIL)) {
			this.sp = new SupervisorPlugin(this.createMILArchitecture()) ;
		} else {
			assert	simArchitectureURI.equals(SimulationArchitectures.SIL) ;
			this.sp = new SupervisorPlugin(this.createSILArchitecture()) ;
		}
		sp.setPluginURI("supervisor") ;
		this.installPlugin(this.sp) ;
		this.logMessage("Supervisor plug-in installed...") ;
	}

	/**
	 * create the MIL simulation architecture supervised by this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the MIL simulation architecture.
	 * @throws Exception	<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	protected ComponentModelArchitecture	createMILArchitecture()
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;

		atomicModelDescriptors.put(
				ElectricMeterMILModel.URI,
				ComponentAtomicModelDescriptor.create(
						ElectricMeterMILModel.URI,
						(Class<? extends EventI>[])
							new Class<?>[]{ConsumptionIntensity.class},
						(Class<? extends EventI>[])
							new Class<?>[]{ConsumptionIntensity.class},
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(
												ElectricMeterMILModel.URI))) ;
		atomicModelDescriptors.put(
				EnergyManagerMILModel.URI,
				ComponentAtomicModelDescriptor.create(
						EnergyManagerMILModel.URI,
						(Class<? extends EventI>[])
							new Class<?>[]{ConsumptionIntensity.class},
						(Class<? extends EventI>[])
							new Class<?>[]{SwitchOn.class,
										   SwitchOff.class,
										   SetLow.class,
										   SetHigh.class},
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(
												EnergyManagerMILModel.URI))) ;
		atomicModelDescriptors.put(
				HairDryerMILCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						HairDryerMILCoupledModel.URI,
						(Class<? extends EventI>[])
							new Class<?>[]{SwitchOn.class,
										   SwitchOff.class,
										   SetLow.class,
										   SetHigh.class},
						(Class<? extends EventI>[])
							new Class<?>[]{ConsumptionIntensity.class},
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(
											HairDryerMILCoupledModel.URI))) ;

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(ElectricMeterMILModel.URI) ;
		submodels.add(EnergyManagerMILModel.URI) ;
		submodels.add(HairDryerMILCoupledModel.URI) ;

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>() ;
		connections.put(
				new EventSource(HairDryerMILCoupledModel.URI,
								ConsumptionIntensity.class),
				new EventSink[] {
						new EventSink(ElectricMeterMILModel.URI,
									  ConsumptionIntensity.class)
						}) ;
		connections.put(
				new EventSource(ElectricMeterMILModel.URI,
								ConsumptionIntensity.class),
				new EventSink[] {
						new EventSink(EnergyManagerMILModel.URI,
								ConsumptionIntensity.class)
				}) ;
		connections.put(
				new EventSource(EnergyManagerMILModel.URI, SwitchOn.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
								SwitchOn.class)	
				}) ;
		connections.put(
				new EventSource(EnergyManagerMILModel.URI, SwitchOff.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
								SwitchOff.class)	
				}) ;
		connections.put(
				new EventSource(EnergyManagerMILModel.URI, SetHigh.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
								SetHigh.class)	
				}) ;
		connections.put(
				new EventSource(EnergyManagerMILModel.URI, SetLow.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
								SetLow.class)	
				}) ;

		coupledModelDescriptors.put(
				SGCoupledModel.URI,
				ComponentCoupledModelDescriptor.create(
						SGCoupledModel.class,
						SGCoupledModel.URI,
						submodels,
						null,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						this.modelURIs2componentURIs.get(
												SGCoupledModel.URI))) ;

		ComponentModelArchitecture arch =
				new ComponentModelArchitecture(
						SimulationArchitectures.MIL,
						SGCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS) ;

		return arch ;
	}

	/**
	 * create the SIL simulation architecture supervised by this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the SIL simulation architecture.
	 * @throws Exception	<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	protected ComponentModelArchitecture	createSILArchitecture()
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;

		atomicModelDescriptors.put(
				ElectricMeterSILModel.URI,
				ComponentAtomicModelDescriptor.create(
						ElectricMeterSILModel.URI,
						(Class<? extends EventI>[])
							new Class<?>[]{ConsumptionIntensity.class},
						null,
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(
												ElectricMeterSILModel.URI))) ;
		atomicModelDescriptors.put(
				EnergyManagerSILModel.URI,
				ComponentAtomicModelDescriptor.create(
						EnergyManagerSILModel.URI,
						null,
						null,
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(
												EnergyManagerSILModel.URI))) ;
		atomicModelDescriptors.put(
				HairDryerSILCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						HairDryerSILCoupledModel.URI,
						null,
						(Class<? extends EventI>[])
							new Class<?>[]{ConsumptionIntensity.class},
						TimeUnit.SECONDS,
						this.modelURIs2componentURIs.get(
											HairDryerSILCoupledModel.URI))) ;

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(ElectricMeterSILModel.URI) ;
		submodels.add(EnergyManagerSILModel.URI) ;
		submodels.add(HairDryerSILCoupledModel.URI) ;

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>() ;
		connections.put(
				new EventSource(HairDryerSILCoupledModel.URI,
								ConsumptionIntensity.class),
				new EventSink[] {
						new EventSink(ElectricMeterSILModel.URI,
									  ConsumptionIntensity.class)
						}) ;

		coupledModelDescriptors.put(
				SGCoupledModel.URI,
				ComponentCoupledModelDescriptor.create(
						SGCoupledModel.class,
						SGCoupledModel.URI,
						submodels,
						null,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						this.modelURIs2componentURIs.get(SGCoupledModel.URI))) ;

		ComponentModelArchitecture arch =
				new ComponentModelArchitecture(
						SimulationArchitectures.SIL,
						SGCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS) ;

		return arch ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("supervisor component begins execution.") ;
		this.sp.createSimulator() ;
		Thread.sleep(1000L) ;
		this.logMessage("supervisor component begins simulation.") ;
		long start = System.currentTimeMillis() ;
		this.sp.setSimulationRunParameters(new HashMap<String, Object>());
		this.sp.doStandAloneSimulation(0, 500.0) ;
		long end = System.currentTimeMillis() ;
		this.logMessage("supervisor component ends simulation. " +
																(end - start)) ;
		Thread.sleep(1000) ;
	}
}
// -----------------------------------------------------------------------------
