package fr.sorbonne_u.components.cyphy.examples.hem.equipments;

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
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.connectors.HairDryerConnector;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.interfaces.HairDryerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.ports.HairDryerOutboundPort;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.connectors.EnergyManagerConnector;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.interfaces.EnergyManagerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.ports.EnergyManagerOutboundPort;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.connectors.ElectricMeterConnector;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.interfaces.ElectricMeterCI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.ports.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>EquipmentDirectory</code> defines global information for
 * the household management component-based architecture, playing the role
 * of a registry in more standard distributed software architectures.
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
public class			EquipmentDirectory
{
	/** the URI to be used to connect equipments to the energy manager.		*/
	public static final String	ENERGY_MANAGER_INBOUNDPORT_URI =
															"emInboundPort" ;
	/** the names of the connector classes given their implemented component
	 *  interfaces.														 	*/
	protected static HashMap<Class<?>,String>	connectorClassNames ;

	// static initialisation of the connector class names; when adding new
	// component interfaces for components representing equipments, add the
	// corresponding entry to the map.
	static {
		connectorClassNames = new HashMap<Class<?>,String>() ;
		connectorClassNames.put(
							EnergyManagerCI.class,
							EnergyManagerConnector.class.getCanonicalName()) ;
		connectorClassNames.put(
							HairDryerCI.class,
							HairDryerConnector.class.getCanonicalName()) ;
		connectorClassNames.put(ElectricMeterCI.class,
							ElectricMeterConnector.class.getCanonicalName()) ;
	}

	/**
	 * create the outbound port for a given component interface inside a given
	 * component; this method is meant to abstract client components from the
	 * actual port classes that are needed to instantiate the ports..
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param inter			component interface required by the component.
	 * @param on			component reference that will hold the port.
	 * @return				the corresponding outbound port.
	 * @throws Exception	<i>to do</i>.
	 */
	public static AbstractOutboundPort	createOutboundPort(
		Class<?> inter,
		ComponentI on
		) throws Exception
	{
		AbstractOutboundPort p = null ;
		if (HairDryerCI.class.isAssignableFrom(inter)) {
			p = new HairDryerOutboundPort(on) ;
		} else if (EnergyManagerCI.class.isAssignableFrom(inter)) {
			p = new EnergyManagerOutboundPort(on) ;
		} else if (ElectricMeterCI.class.isAssignableFrom(inter)) {
			p = new ElectricMeterOutboundPort(on) ;
		} else {
			throw new Exception("unknown equipment") ;
		}
		return p ;
	}

	/**
	 * return name of the connector class to be used to instantiate the connector.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	inter != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param inter			component interface implemented by the connector.
	 * @return				name of the connector class to be used to instantiate the connector.
	 * @throws Exception	<i>to do</i>.
	 */
	public static String	connectorClassName(Class<?> inter) throws Exception
	{
		assert	inter != null ;

		String name = connectorClassNames.get(inter) ;
		if (name == null) {
			for (HashMap.Entry<Class<?>,String> e :
											connectorClassNames.entrySet()) {
				if (e.getKey().isAssignableFrom(inter)) {
					name = e.getValue() ;
					break ;
				}
			}
		}
		if (name != null) {
			return name ;
		} else {
			throw new Exception("unknown equipment") ;
		}
	}
}
// -----------------------------------------------------------------------------
