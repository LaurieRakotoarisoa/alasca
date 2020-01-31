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

import java.util.HashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
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

//------------------------------------------------------------------------------
/**
 * The class <code>DistributedCVM</code> deploys the Molene example as
 * a multi-JVM execution using currently three JVMs.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * See the class CVM for more information about the example.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-07-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			DistributedCVM
extends		AbstractDistributedCVM
{
	public static final String	PC_JVM_URI = "pc" ;
	public static final String	SERVER_JVM_URI = "server" ;
	public static final String	SUPERVISOR_JVM_URI = "supervisor" ;

	public static final String	NBC_URI = "network-bandwidth-component" ;
	public static final String	PC_URI = "portable-computer-component" ;
	public static final String	PCC_URI = "portable-computer-controller-component" ;
	public static final String	SERVER_COMPONENT_URI = "server-component" ;
	public static final String	SERVER_CONTROLLER_COMPONENT_URI = "server-controller-component" ;
	public static final String	NC_URI = "network-component" ;
	public static final String	CC_URI = "coordinator-component" ;
	public static final String	SC_URI = "supervisor-component" ;

	public				DistributedCVM(String[] args) throws Exception
	{
		super(args);
	}

	public				DistributedCVM(String[] args, int xLayout, int yLayout)
	throws Exception
	{
		super(args, xLayout, yLayout);
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(PC_JVM_URI)) {

			AbstractComponent.createComponent(
					NetworkBandwidthComponent.class.getCanonicalName(),
					new Object[]{NBC_URI}) ;
			AbstractComponent.createComponent(
					PCComponent.class.getCanonicalName(),
					new Object[]{PC_URI}) ;
			AbstractComponent.createComponent(
					ControllerComponent.class.getCanonicalName(),
					new Object[]{PCC_URI,true}) ;
			AbstractComponent.createComponent(
					NetworkComponent.class.getCanonicalName(),
					new Object[]{NC_URI}) ;

		} else if (thisJVMURI.equals(SERVER_JVM_URI)) {

			AbstractComponent.createComponent(
					ServerComponent.class.getCanonicalName(),
					new Object[]{SERVER_COMPONENT_URI}) ;
			AbstractComponent.createComponent(
					ControllerComponent.class.getCanonicalName(),
					new Object[]{SERVER_CONTROLLER_COMPONENT_URI,false}) ;

		} else if (thisJVMURI.equals(SUPERVISOR_JVM_URI)) {

			AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[]{CC_URI}) ;

			HashMap<String,String> hm = new HashMap<>() ;
			hm.put(WiFiModel.URI, NBC_URI) ;
			hm.put(PortableComputerModel.URI, PC_URI) ;
			hm.put(ControllerModel.PORTABLE_URI, PCC_URI) ;
			hm.put(ServerModel.URI, SERVER_COMPONENT_URI) ;
			hm.put(ControllerModel.SERVER_URI,
				   SERVER_CONTROLLER_COMPONENT_URI) ;
			hm.put(NetworkModel.URI, NC_URI) ;
			hm.put(MoleneModel.URI, CC_URI) ;

			AbstractComponent.createComponent(
					MoleneSupervisorComponent.class.getCanonicalName(),
					new Object[]{hm}) ;

		} else {

			System.out.println("Unknown JVM URI = " + thisJVMURI) ;

		}

		super.instantiateAndPublish();
	}

	public static void	main(String[] args)
	{
		try {
			DistributedCVM dcvm = new DistributedCVM(args) ;
			dcvm.startStandardLifeCycle(120000L) ;
			Thread.sleep(150000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
//------------------------------------------------------------------------------
