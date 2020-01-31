package fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.components;

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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.models.ElectricMeterMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.ports.ElectricMeterInboundPort;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.sil.models.ElectricMeterSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeter</code> implements an electric meter as a
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
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = {ElectricMeterCI.class})
// -----------------------------------------------------------------------------
public class			ElectricMeter
extends		AbstractCyPhyComponent
implements	EmbeddingComponentAccessI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** inbound port offering the services of the electric meter.			*/
	protected ElectricMeterInboundPort					emInboundPort ;
	/** atomic simulation plug-in holding the simulation model attached
	 *  to the component.													*/
	protected AtomicSimulatorPlugin						asp ;
	/** the simulation mode of the current execution of the component.		*/
	protected SimulationArchitectures.SimulationMode	simMode ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an electric meter component.
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
	protected			ElectricMeter(String simArchitectureURI)
	throws Exception
	{
		super(2, 0) ;

		assert	simArchitectureURI != null ;

		this.initialise(simArchitectureURI) ;
	}

	/**
	 * create an electric meter component.
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
	protected			ElectricMeter(
		String reflectionInboundPortURI,
		String simArchitectureURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0) ;

		assert	simArchitectureURI != null ;

		this.initialise(simArchitectureURI) ;
	}

	/**
	 * initialise the electric meter component.
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
		assert	simArchitectureURI != null ;

		if (!simArchitectureURI.equals(SimulationArchitectures.NONE)) {
			String modelURI = null ;
			if (simArchitectureURI.equals(SimulationArchitectures.MIL)) {
				modelURI = ElectricMeterMILModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.MIL ;
			} else if (simArchitectureURI.equals(SimulationArchitectures.SIL)) {
				modelURI = ElectricMeterSILModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.SIL ;
			}
			Architecture localArchitecture =
									this.createLocalArchitecture(modelURI) ;
			this.asp = new ElectricMeterSimulatorPlugin() ;
			this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
			this.asp.setSimulationArchitecture(localArchitecture) ;
			this.installPlugin(this.asp) ;
		} else {
			this.simMode = null ;
		}
		this.emInboundPort = new ElectricMeterInboundPort(this) ;
		this.emInboundPort.publishPort() ;

		// Toggle logging on to get a log on the screen.
		this.tracer.setTitle("Electric meter") ;
		this.tracer.setRelativePosition(2, 0) ;
		this.toggleTracing() ;	
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.emInboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown() ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.emInboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

	// -------------------------------------------------------------------------
	// Component services
	// -------------------------------------------------------------------------

	/**
	 * the electricity consumption sensor returning the current global
	 * electricity consumption as measured by this meter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the global electricity consumption.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		intensitySensor() throws Exception
	{
		double level = -1.0 ;
		if (this.simMode == SimulationArchitectures.SimulationMode.SIL) {
			// when performing software-in-the-loop simulations, the actual
			// electricity metering is replaced by the simulated value given
			// by the corresponding simulation model.
			level = (double) this.asp.getModelStateValue(
						ElectricMeterSILModel.URI,
						ElectricMeterSimulatorPlugin.INTENSITY_VARIABLE_NAME) ;
		} else {
			// when performing model-in-the-loop simulation, this method will
			// not be called; when no simulation is performed, a hardware
			// sensor would be called and the following line would need to be
			// replaced by a call to this hardware sensor.
			level = 0.0 ;
		}
		this.traceMessage("electric meter read " + level + ".\n") ;
		return level ;
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
		if (modelURI.equals(ElectricMeterMILModel.URI)) {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;
			atomicModelDescriptors.put(
					ElectricMeterMILModel.URI,
					AtomicModelDescriptor.create(
							ElectricMeterMILModel.class,
							ElectricMeterMILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			Architecture localArchitecture =
					new Architecture(
							ElectricMeterMILModel.URI,
							atomicModelDescriptors,
							new HashMap<>(),
							TimeUnit.SECONDS) ;
			return localArchitecture ;
		} else if (modelURI.equals(ElectricMeterSILModel.URI)) {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;
			atomicModelDescriptors.put(
					ElectricMeterSILModel.URI,
					AtomicModelDescriptor.create(
							ElectricMeterSILModel.class,
							ElectricMeterSILModel.URI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			Architecture localArchitecture =
					new Architecture(
							ElectricMeterSILModel.URI,
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
