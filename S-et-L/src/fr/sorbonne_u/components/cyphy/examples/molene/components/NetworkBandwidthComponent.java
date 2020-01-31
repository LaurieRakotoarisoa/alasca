package fr.sorbonne_u.components.cyphy.examples.molene.components;

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

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.simulations.MoleneAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbm.WiFiBandwidthModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthSensorModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.WiFiDisconnectionModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wm.WiFiModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>NetworkBandwidthComponent</code> implements the network
 * bandwidth sensor for the Molene example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			NetworkBandwidthComponent
extends		AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a network bandwidth component.
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
	protected			NetworkBandwidthComponent() throws Exception
	{
		super(1, 0) ;
		this.initialise() ;
	}

	/**
	 * create a network bandwidth component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the created component.
	 * @throws Exception 				<i>to do</i>.
	 */
	protected			NetworkBandwidthComponent(
		String reflectionInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		this.initialise() ;
	}

	/**
	 * initialise the network bandwidth component.
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
	protected void		initialise() throws Exception
	{
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		AtomicSimulatorPlugin asp = new MoleneAtomicSimulatorPlugin() ;
		asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(asp) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture	createLocalArchitecture(String architectureURI)
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;

		atomicModelDescriptors.put(
				WiFiDisconnectionModel.URI,
				AtomicModelDescriptor.create(
						WiFiDisconnectionModel.class,
						WiFiDisconnectionModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		atomicModelDescriptors.put(
				WiFiBandwidthModel.URI,
				AtomicHIOA_Descriptor.create(
						WiFiBandwidthModel.class,
						WiFiBandwidthModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		atomicModelDescriptors.put(
				TicModel.URI + "-1",
				AtomicModelDescriptor.create(
						TicModel.class,
						TicModel.URI + "-1",
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		atomicModelDescriptors.put(
				WiFiBandwidthSensorModel.URI,
				AtomicHIOA_Descriptor.create(
						WiFiBandwidthSensorModel.class,
						WiFiBandwidthSensorModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<String,CoupledModelDescriptor>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(WiFiDisconnectionModel.URI) ;
		submodels.add(WiFiBandwidthModel.URI) ;
		submodels.add(TicModel.URI + "-1") ;
		submodels.add(WiFiBandwidthSensorModel.URI) ;

		Map<Class<? extends EventI>,EventSink[]> imported =
				new HashMap<Class<? extends EventI>,EventSink[]>() ;

		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;

		reexported.put(
				WiFiBandwidthReading.class,
				new ReexportedEvent(WiFiBandwidthSensorModel.URI,
						WiFiBandwidthReading.class)) ;
		reexported.put(
				InterruptionEvent.class,
				new ReexportedEvent(WiFiDisconnectionModel.URI,
						InterruptionEvent.class)) ;
		reexported.put(
				ResumptionEvent.class,
				new ReexportedEvent(WiFiDisconnectionModel.URI,
						ResumptionEvent.class)) ;

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>() ;
		EventSource from1 = new EventSource(WiFiDisconnectionModel.URI,
											InterruptionEvent.class) ;
		EventSink[] to1 = new EventSink[] {
								new EventSink(WiFiBandwidthModel.URI,
											  InterruptionEvent.class)} ;
		connections.put(from1, to1) ;
		EventSource from2 = new EventSource(WiFiDisconnectionModel.URI,
											ResumptionEvent.class) ;
		EventSink[] to2 = new EventSink[] {
								new EventSink(WiFiBandwidthModel.URI,
											  ResumptionEvent.class)} ;
		connections.put(from2, to2) ;
		EventSource from3 = new EventSource(TicModel.URI + "-1",
											TicEvent.class) ;
		EventSink[] to3 = new EventSink[] {
								new EventSink(WiFiBandwidthSensorModel.URI,
											  TicEvent.class)} ;
		connections.put(from3, to3) ;

		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>() ;
		VariableSource source1 = new VariableSource("bandwidth",
													Double.class,
													WiFiBandwidthModel.URI) ;
		VariableSink[] sinks1 = new VariableSink[] {
										new VariableSink(
												"bandwidth",
												Double.class,
												WiFiBandwidthSensorModel.URI)} ;
		bindings.put(source1, sinks1) ;

		coupledModelDescriptors.put(
				WiFiModel.URI,
				new CoupledHIOA_Descriptor(
							WiFiModel.class,
							WiFiModel.URI,
							submodels,
							imported,
							reexported,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE,
							null,
							null,
							bindings)) ;

		Architecture localArchitecture =
				new Architecture(
							WiFiModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;

		return localArchitecture ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("network bandwidth component begins execution.") ;
	}
}
// -----------------------------------------------------------------------------
