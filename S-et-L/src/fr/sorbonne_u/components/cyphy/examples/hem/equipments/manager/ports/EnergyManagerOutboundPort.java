package fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.ports;

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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.interfaces.EnergyManagerCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>EnergyManagerOutboundPort</code> implements the outbound port
 * for the <code>EnergyManagerCI</code> component interface.
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
public class			EnergyManagerOutboundPort
extends		AbstractOutboundPort
implements	EnergyManagerCI
{
	private static final long serialVersionUID = 1L;

	public				EnergyManagerOutboundPort(ComponentI owner)
	throws Exception
	{
		super(EnergyManagerCI.class, owner);
	}

	public				EnergyManagerOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, EnergyManagerCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.components.EnergyManagerImplementationI#connect(java.lang.Class, java.lang.String)
	 */
	@Override
	public void			connect(Class<?> inter, String inboundPortURI)
	throws Exception
	{
		((EnergyManagerCI)this.connector).connect(inter, inboundPortURI) ;
	}
}
// -----------------------------------------------------------------------------