package fr.sorbonne_u.components.cyphy.examples.molene;

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

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.examples.molene.components.ControllerComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.components.CoordinatorComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.components.MoleneSupervisorComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.components.NetworkBandwidthComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.components.NetworkComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.components.PCComponent;
import fr.sorbonne_u.components.cyphy.examples.molene.components.ServerComponent;
import fr.sorbonne_u.devs_simulation.examples.molene.MoleneModel;
import fr.sorbonne_u.devs_simulation.examples.molene.controllers.ControllerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.nm.NetworkModel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcm.PortableComputerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.sm.ServerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wm.WiFiModel;
import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;

//-----------------------------------------------------------------------------
/**
 * The class <code>CVM</code> deploys the Molene example on a single JVM for
 * execution.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The Molene example in BCM uses simulation models already developed for the
 * DEVS simulation library to attach them to components. Here, eight components
 * are used corresponding to their definitional classes and each holding a
 * corresponding simulation model:
 * </p>
 * <ol>
 * <li><code>NetworkBandwidthComponent</code>: correspond to the environment
 *   from which a network bandwidth level would be obtained through a sensor.
 *   </li>
 * <li><code>PCComponent</code>: the PC exchanging data with the server.</li>
 * <li><code>ControllerComponent</code>: implementing the controller deciding
 *   the mode of data transmission (compressing, not compressing or not
 *   compressing anymore until the end of the process); it exists in
 *   two distinct copies, one for the PC the other for the server.</li>
 * <li><code>ServerComponent</code>: the server exchanging data with the PC.
 *   </li>
 * <li><code>NetworkComponent</code>: simulates the network as message
 *   transmission medium; currently it only imposes a random transmission
 *   delay and it replaces the actual network for MIL and SIL simulations.
 *   </li>
 * <li><code>CoordinatorComponent</code>: for DEVS simulation purposes,
 *   this component holds the coupled model composing all of the other
 *   models and will coordinate them during simulation runs.</li>
 * <li><code>MoleneSupervisorComponent</code>:for DEVS simulation purposes,
 *   this component supervises <i>i.e.</i>, creates and interconnects the
 *   simulators and manages the simulation runs.</li>
 * </ol>
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
public class				CVM
extends		AbstractCVM
{
	public				CVM() throws Exception
	{
		super() ;
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		HashMap<String,String> hm = new HashMap<>() ;

		String nbcURI =
			AbstractComponent.createComponent(
				NetworkBandwidthComponent.class.getCanonicalName(),
				new Object[]{}) ;
		hm.put(WiFiModel.URI, nbcURI) ;

		String pcURI =
			AbstractComponent.createComponent(
				PCComponent.class.getCanonicalName(),
				new Object[]{}) ;
		hm.put(PortableComputerModel.URI, pcURI) ;

		String pcContURI =
			AbstractComponent.createComponent(
				ControllerComponent.class.getCanonicalName(),
				new Object[]{true}) ;
		hm.put(ControllerModel.PORTABLE_URI, pcContURI) ;

		String serverURI =
			AbstractComponent.createComponent(
				ServerComponent.class.getCanonicalName(),
				new Object[]{}) ;
		hm.put(ServerModel.URI, serverURI) ;

		String serverContURI =
			AbstractComponent.createComponent(
				ControllerComponent.class.getCanonicalName(),
				new Object[]{false}) ;
		hm.put(ControllerModel.SERVER_URI, serverContURI) ;

		String networkURI =
			AbstractComponent.createComponent(
				NetworkComponent.class.getCanonicalName(),
				new Object[]{}) ;
		hm.put(NetworkModel.URI, networkURI) ;

		String coordURI =
			AbstractComponent.createComponent(
				CoordinatorComponent.class.getCanonicalName(),
				new Object[]{}) ;
		hm.put(MoleneModel.URI, coordURI) ;

		AbstractComponent.createComponent(
				MoleneSupervisorComponent.class.getCanonicalName(),
				new Object[]{hm}) ;

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVM vm = new CVM() ;
			vm.startStandardLifeCycle(40000L) ;
			Thread.sleep(50000L) ;
			System.out.println("stopping...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
//-----------------------------------------------------------------------------
