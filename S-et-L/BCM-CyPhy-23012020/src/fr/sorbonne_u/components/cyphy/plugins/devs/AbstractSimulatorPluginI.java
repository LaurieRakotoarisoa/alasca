package fr.sorbonne_u.components.cyphy.plugins.devs;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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

import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorPluginManagementI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

//-----------------------------------------------------------------------------
/**
 * The interface <code>AbstractSimulatorPluginI</code> declares the core
 * services of the DEVS simulator plug-in.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface gathers the different interfaces a supervisor plug-in
 * must implement to provide the behaviours required by this role in a DEVS
 * simulation context:
 * </p>
 * <ul>
 * <li><code>SimulatorI</code> declares methods used by other simulator
 *   components to perform simulation steps.</li>
 * <li><code>SimulatorPluginManagementI</code> declares methods used from
 *   the supervisor plug-in in order to manage simulation runs by calling
 *   the simulators on components holding them.</li>
 * <li><code>ModelStateAccessI</code> declares methods used by components
 *   holding simulation models to access their state during simulation
 *   runs when performing software-in-the-loop simulations.</li>
 * </ul>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		AbstractSimulatorPluginI
extends		PluginI,
			SimulatorI,
			SimulatorPluginManagementI,
			ModelStateAccessI
{
	public String		getSimulatorPluginManagementInboundPortURI()
	throws Exception ;
	public String		getSimulatorInboundPortURI() throws Exception ;
}
//-----------------------------------------------------------------------------
