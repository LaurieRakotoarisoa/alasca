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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.MoleneModel;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatteryLevel;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatterySensorModel;
import fr.sorbonne_u.devs_simulation.examples.molene.controllers.ControllerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.nm.NetworkModel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcm.PortableComputerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.Compressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.LowBattery;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.NotCompressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.PortableComputerStateModel;
import fr.sorbonne_u.devs_simulation.examples.molene.sm.ServerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbm.WiFiBandwidthModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthSensorModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.WiFiDisconnectionModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wm.WiFiModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.utils.PlotterDescription;

// -----------------------------------------------------------------------------
/**
 * The class <code>SupervisorComponent</code> implements the components that
 * act as the supervisor of the global simulation architecture in the
 * Molene example.
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
public class			MoleneSupervisorComponent
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String		ARCHITECTURE_URI = "MoleneMILArchitecture" ;
	/** the supervisor plug-in attached to the component.					*/
	protected SupervisorPlugin		sp ;
	/** maps from URIs of models to URIs of the reflection inbound ports
	 *  of the components that hold them.									*/
	protected Map<String,String>	modelURIs2componentURIs ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor component for the Molene example from the given
	 * map describing the deployment of models on components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURIs2componentURIs != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURIs2componentURIs	map from model URIs to the URIs of the components that hold them.
	 * @throws Exception				<i>to do</i>.
	 */
	protected 			MoleneSupervisorComponent(
			Map<String,String> modelURIs2componentURIs
			) throws Exception
		{
			super(2, 0) ;

			assert	modelURIs2componentURIs != null ;
			this.initialise(modelURIs2componentURIs) ;
		}

	/**
	 * create a supervisor component for the Molene example with the given
	 * reflection inbound port URI and from the given map describing the
	 * deployment of models on components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * pre	modelURIs2componentURIs != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port imposed for the creation of this component.
	 * @param modelURIs2componentURIs	map from model URIs to the URIs of the components that hold them.
	 * @throws Exception				<i>to do</i>.
	 */
	protected 			MoleneSupervisorComponent(
		String reflectionInboundPortURI,
		Map<String,String> modelURIs2componentURIs
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0) ;

		this.initialise(modelURIs2componentURIs) ;
	}

	/**
	 * initialise the simulation architecture for the Molene example from the
	 * given map describing the deployment of models on components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURIs2componentURIs != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURIs2componentURIs	map from model URIs to the URIs of the components that hold them.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		Map<String,String> modelURIs2componentURIs
		) throws Exception
	{
		this.modelURIs2componentURIs = modelURIs2componentURIs ;

		this.tracer.setTitle("SupervisorComponent") ;
		this.tracer.setRelativePosition(0, 4) ;
		this.toggleTracing() ;

		this.sp = new SupervisorPlugin(this.createArchitecture()) ;
		sp.setPluginURI("supervisor") ;
		this.installPlugin(this.sp) ;
		this.logMessage("Supervisor plug-in installed...") ;
	}

	/**
	 * create the architecture of the Molene simulation that will be supervised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the architecture of the Molene example.
	 * @throws Exception	<i>todo</i>.
	 */
	@SuppressWarnings("unchecked")
	protected ComponentModelArchitecture	createArchitecture()
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;

		atomicModelDescriptors.put(
			WiFiModel.URI,
			ComponentAtomicModelDescriptor.create(
					WiFiModel.URI,
					null,
					(Class<? extends EventI>[])
						new Class<?>[]{
							WiFiBandwidthReading.class,
							InterruptionEvent.class,
							ResumptionEvent.class
						},
					TimeUnit.SECONDS,
					modelURIs2componentURIs.get(WiFiModel.URI))) ;
		atomicModelDescriptors.put(
			PortableComputerModel.URI,
			ComponentAtomicModelDescriptor.create(
					PortableComputerModel.URI,
					(Class<? extends EventI>[])
						new Class<?>[]{
							InterruptionEvent.class,
							ResumptionEvent.class,
							Compressing.class,
							NotCompressing.class,
							LowBattery.class
						},
					(Class<? extends EventI>[])
						new Class<?>[]{
							BatteryLevel.class
						},
					TimeUnit.SECONDS,
					modelURIs2componentURIs.get(PortableComputerModel.URI))) ;
		atomicModelDescriptors.put(
			ControllerModel.PORTABLE_URI,
			ComponentAtomicModelDescriptor.create(
					ControllerModel.PORTABLE_URI,
					(Class<? extends EventI>[])
						new Class<?>[]{
							BatteryLevel.class,
							WiFiBandwidthReading.class
						},
					(Class<? extends EventI>[])
						new Class<?>[]{
							Compressing.class,
							NotCompressing.class,
							LowBattery.class
						},
					TimeUnit.SECONDS,
					modelURIs2componentURIs.get(
										ControllerModel.PORTABLE_URI))) ;
		atomicModelDescriptors.put(
			ServerModel.URI,
			ComponentAtomicModelDescriptor.create(
					ServerModel.URI,
					(Class<? extends EventI>[])
						new Class<?>[]{
							Compressing.class,
							NotCompressing.class,
							LowBattery.class
						},
					null,
					TimeUnit.SECONDS, 
					modelURIs2componentURIs.get(ServerModel.URI))) ;
		atomicModelDescriptors.put(
			ControllerModel.SERVER_URI,
			ComponentAtomicModelDescriptor.create(
					ControllerModel.SERVER_URI,
					(Class<? extends EventI>[])
						new Class<?>[]{
							BatteryLevel.class,
							WiFiBandwidthReading.class
						},
					(Class<? extends EventI>[])
						new Class<?>[]{
							Compressing.class,
							NotCompressing.class,
							LowBattery.class
						},
					TimeUnit.SECONDS,
					modelURIs2componentURIs.get(ControllerModel.SERVER_URI))) ;
		atomicModelDescriptors.put(
			NetworkModel.URI,
			ComponentAtomicModelDescriptor.create(
					NetworkModel.URI,
					(Class<? extends EventI>[])
						new Class<?>[]{
							BatteryLevel.class,
							WiFiBandwidthReading.class
						},
					(Class<? extends EventI>[])
						new Class<?>[]{
							BatteryLevel.class,
							WiFiBandwidthReading.class
						},
					TimeUnit.SECONDS,
					modelURIs2componentURIs.get(NetworkModel.URI))) ;

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>() ;

		Set<String> submodels3 = new HashSet<String>() ;
		submodels3.add(WiFiModel.URI) ;
		submodels3.add(PortableComputerModel.URI) ;
		submodels3.add(ControllerModel.PORTABLE_URI) ;
		submodels3.add(ControllerModel.SERVER_URI) ;
		submodels3.add(ServerModel.URI) ;
		submodels3.add(NetworkModel.URI) ;

		Map<EventSource,EventSink[]> connections3 =
							new HashMap<EventSource,EventSink[]>() ;

		EventSource from31 =
				new EventSource(
						WiFiModel.URI,
						WiFiBandwidthReading.class) ;
		EventSink[] to31 =
				new EventSink[] {
						new EventSink(
								ControllerModel.PORTABLE_URI,
								WiFiBandwidthReading.class),
						new EventSink(
								NetworkModel.URI,
								WiFiBandwidthReading.class)} ;
		connections3.put(from31, to31) ;
		EventSource from311 =
				new EventSource(
						WiFiModel.URI,
						InterruptionEvent.class) ;
		EventSink[] to311 =
				new EventSink[] {
						new EventSink(
								PortableComputerModel.URI,
								InterruptionEvent.class)} ;
		connections3.put(from311, to311) ;
		EventSource from312 =
				new EventSource(
						WiFiModel.URI,
						ResumptionEvent.class) ;
		EventSink[] to312 =
				new EventSink[] {
						new EventSink(
								PortableComputerModel.URI,
								ResumptionEvent.class)} ;
		connections3.put(from312, to312) ;
		EventSource from32 =
				new EventSource(
						PortableComputerModel.URI,
						BatteryLevel.class) ;
		EventSink[] to32 =
				new EventSink[] {
						new EventSink(
								ControllerModel.PORTABLE_URI,
								BatteryLevel.class),
						new EventSink(
								NetworkModel.URI,
								BatteryLevel.class)} ;
		connections3.put(from32, to32) ;
		EventSource from33 =
				new EventSource(
						ControllerModel.PORTABLE_URI,
						Compressing.class) ;
		EventSink[] to33 =
				new EventSink[] {
						new EventSink(
								PortableComputerModel.URI,
								Compressing.class)} ;
		connections3.put(from33, to33) ;
		EventSource from34 =
				new EventSource(
						ControllerModel.PORTABLE_URI,
						NotCompressing.class) ;
		EventSink[] to34 =
				new EventSink[] {
						new EventSink(
								PortableComputerModel.URI,
								NotCompressing.class)} ;
		connections3.put(from34, to34) ;
		EventSource from35 =
				new EventSource(
						ControllerModel.PORTABLE_URI,
						LowBattery.class) ;
		EventSink[] to35 =
				new EventSink[] {
						new EventSink(
								PortableComputerModel.URI,
								LowBattery.class)} ;
		connections3.put(from35, to35) ;
		EventSource from36 =
				new EventSource(
						NetworkModel.URI,
						WiFiBandwidthReading.class) ;
		EventSink[] to36 =
				new EventSink[] {
						new EventSink(
								ControllerModel.SERVER_URI,
								WiFiBandwidthReading.class)} ;
		connections3.put(from36, to36) ;
		EventSource from37 =
				new EventSource(
						NetworkModel.URI,
						BatteryLevel.class) ;
		EventSink[] to37 =
				new EventSink[] {
						new EventSink(
								ControllerModel.SERVER_URI,
								BatteryLevel.class)} ;
		connections3.put(from37, to37) ;
		EventSource from38 =
				new EventSource(
						ControllerModel.SERVER_URI,
						Compressing.class) ;
		EventSink[] to38 =
				new EventSink[] {
						new EventSink(
								ServerModel.URI,
								Compressing.class)} ;
		connections3.put(from38, to38) ;
		EventSource from39 =
				new EventSource(
						ControllerModel.SERVER_URI,
						NotCompressing.class) ;
		EventSink[] to39 =
				new EventSink[] {
						new EventSink(
								ServerModel.URI,
								NotCompressing.class)} ;
		connections3.put(from39, to39) ;
		EventSource from310 =
				new EventSource(
						ControllerModel.SERVER_URI,
						LowBattery.class) ;
		EventSink[] to310 =
				new EventSink[] {
						new EventSink(
								ServerModel.URI,
								LowBattery.class)} ;
		connections3.put(from310, to310) ;

		coupledModelDescriptors.put(
				MoleneModel.URI,
				ComponentCoupledModelDescriptor.create(
						MoleneModel.class,
						MoleneModel.URI,
						submodels3,
						null,
						null,
						connections3,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						modelURIs2componentURIs.get(MoleneModel.URI))) ;

		return new ComponentModelArchitecture(
						ARCHITECTURE_URI,
						MoleneModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS) ;

	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("supervisor component begins execution.") ;

		sp.createSimulator() ;

		Thread.sleep(1000L) ;

		Map<String, Object> simParams = new HashMap<String, Object>() ;

		String modelURI = TicModel.URI  + "-1" ;
		simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
					  new Duration(10.0, TimeUnit.SECONDS)) ;

		modelURI = WiFiDisconnectionModel.URI ;
		simParams.put(modelURI + ":" + WiFiDisconnectionModel.MTBI, 200.0) ;
		simParams.put(modelURI + ":" + WiFiDisconnectionModel.MID, 10.0) ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"WiFi Disconnection Model",
						"Time (sec)",
						"Connected/Interrupted",
						SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y,
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = WiFiBandwidthModel.URI ;
		simParams.put(
				modelURI + ":" + WiFiBandwidthModel.MAX_BANDWIDTH, 50.0) ;
		simParams.put(modelURI + ":" + WiFiBandwidthModel.BAAR, 1.75) ;
		simParams.put(modelURI + ":" + WiFiBandwidthModel.BBAR, 1.75) ;
		simParams.put(modelURI + ":" + WiFiBandwidthModel.BMASSF, 1.0/11.0) ;
		simParams.put(modelURI + ":" + WiFiBandwidthModel.BIS, 0.5) ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"WiFi Bandwith Model",
						"Time (sec)",
						"Bandwith (Mbps)",
						SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y +
								SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = WiFiBandwidthSensorModel.URI ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"WiFi Bandwith Sensor Model",
						"Time (sec)",
						"Bandwith (Mbps)",
						SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y +
								2*SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = TicModel.URI  + "-2" ;
		simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
					  new Duration(50.0, TimeUnit.SECONDS)) ;

		modelURI = PortableComputerStateModel.URI ;
		simParams.put(
				modelURI + ":" +
					PortableComputerStateModel.BATTERY_PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Portable Computer Model - Battery Level",
						"Time (sec)",
						"Battery level (mAh)",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y,
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;
		simParams.put(
				modelURI + ":" +
					PortableComputerStateModel.STATE_PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Portable Computer Model - State",
						"Time (sec)",
						"State",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = BatterySensorModel.URI ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Battery Level Sensor Model",
						"Time (sec)",
						"Battery level (mAh)",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = ControllerModel.PORTABLE_URI ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Portable Computer Controller Model",
						"Time (sec)",
						"Decision",
						SimulationMain.ORIGIN_X +
					  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							3 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = ServerModel.URI ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Server Model",
						"Time (sec)",
						"State",
						SimulationMain.ORIGIN_X +
					  		2 * SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = ControllerModel.SERVER_URI ;
		simParams.put(
				modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Server Controller Model",
						"Time (sec)",
						"Decision",
						SimulationMain.ORIGIN_X +
					  		2 * SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
							3 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight())) ;

		modelURI = NetworkModel.URI ;
		simParams.put(
				modelURI + ":" + NetworkModel.GAMMA_SHAPE_PARAM_NAME,
				11.0) ;
		simParams.put(
				modelURI + ":" + NetworkModel.GAMMA_SCALE_PARAM_NAME,
				2.0) ;

		this.logMessage("supervisor component begins first simulation.") ;
		sp.setSimulationRunParameters(simParams) ;
		long start = System.currentTimeMillis() ;
		sp.doStandAloneSimulation(0, 5000) ;
		long end = System.currentTimeMillis() ;
		this.logMessage("supervisor component ends first simulation. " +
																(end - start)) ;
		Thread.sleep(2000) ;
		// Here is an example of performing a second simulation run one the
		// same simulation architecture.
		// Before starting a new run, cleanup the simulators and models.
		sp.finaliseSimulation() ;

		this.logMessage("supervisor component begins second simulation.") ;
		sp.setSimulationRunParameters(simParams) ;
		start = System.currentTimeMillis() ;
		sp.doStandAloneSimulation(0, 5000) ;
		end = System.currentTimeMillis() ;
		this.logMessage("supervisor component ends second simulation. " +
																(end - start)) ;
		Thread.sleep(2000) ;
		// Before starting a new run, cleanup the simulators and models.
		sp.finaliseSimulation() ;

		// Here is an example of a change in the simulation architecture.
		// Before starting a new run, the simulation architecture is reset
		// and a new instantiation is performed.
		sp.resetArchitecture(this.createArchitecture()) ;
		this.logMessage("supervisor component begins third simulation.") ;
		sp.setSimulationRunParameters(simParams) ;
		start = System.currentTimeMillis() ;
		sp.doStandAloneSimulation(0, 5000) ;
		end = System.currentTimeMillis() ;
		this.logMessage("supervisor component ends third simulation. " +
																(end - start)) ;
	}
}
// -----------------------------------------------------------------------------
