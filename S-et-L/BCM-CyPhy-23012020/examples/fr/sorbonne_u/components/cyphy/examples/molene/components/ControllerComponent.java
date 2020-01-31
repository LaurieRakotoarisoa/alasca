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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.simulations.MoleneAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.controllers.ControllerModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;

// -----------------------------------------------------------------------------
/**
 * The class <code>ControllerComponent</code> implements the controller
 * components, for the PC and for the server, in the Molene example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-06-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ControllerComponent
extends		AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** true if the component is controlling the PC, false if it is
	 *  controlling the server.												*/
	protected final boolean		isPCController ;
	/** URI of the simulator plug-in and the simulation model held by the
	 *  component.															*/
	protected final String		pluginURI ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a controller component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param isPCController	true if the component controls the PC component.
	 * @throws Exception		<i>to do</i>.
	 */
	protected			ControllerComponent(boolean isPCController)
	throws Exception
	{
		super(1, 0) ;
		this.isPCController = isPCController ;
		this.pluginURI =	isPCController ?
								ControllerModel.PORTABLE_URI
								:	ControllerModel.SERVER_URI ;
		this.initialise() ;
	}

	/**
	 * create a controller component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the new component.
	 * @param isPCController			true if the component controls the PC component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			ControllerComponent(
		String reflectionInboundPortURI,
		boolean isPCController
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		this.isPCController = isPCController ;
		this.pluginURI =	isPCController ?
								ControllerModel.PORTABLE_URI
							:	ControllerModel.SERVER_URI ;
		this.initialise() ;

	}

	/**
	 * initialise the Controller component.
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
		String modelURI = null ;
		if (this.isPCController) {
			modelURI = ControllerModel.PORTABLE_URI ;
		} else {
			modelURI = ControllerModel.SERVER_URI ;
		}
		Architecture localArchitecture =
									this.createLocalArchitecture(modelURI) ;
		AtomicSimulatorPlugin asp = new MoleneAtomicSimulatorPlugin() ;
		asp.setPluginURI(this.pluginURI) ;
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
	protected Architecture	createLocalArchitecture(String modelURI)
	throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;

		atomicModelDescriptors.put(
					modelURI,
					AtomicModelDescriptor.create(
							ControllerModel.class,
							modelURI,
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

		Architecture localArchitecture =
					new Architecture(
							modelURI,
							atomicModelDescriptors,
							new HashMap<>(),
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

		this.logMessage("Controller component " + this.pluginURI
													+ " begins execution.") ;
	}
}
// -----------------------------------------------------------------------------
