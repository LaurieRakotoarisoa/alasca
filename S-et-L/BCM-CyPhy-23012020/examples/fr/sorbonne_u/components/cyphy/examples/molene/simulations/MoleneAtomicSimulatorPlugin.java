package fr.sorbonne_u.components.cyphy.examples.molene.simulations;

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

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.examples.molene.interfaces.MoleneModelImplementationI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

// -----------------------------------------------------------------------------
/**
 * The class <code>MoleneAtomicSimulatorPlugin</code> implements the behaviours
 * required for Mole atomic simulator plugins.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The Mole atomic models have plotters that need to be disposed to free
 * the resources they use and to disappear from the screen. Hence, the
 * method <code>finaliseSimulation</code> call the method
 * <code>disposePlotters</code> implemented by Molene models to perform
 * this. An alternative would be to create subclasses of the simulation engine
 * classes, but this would be more involving both by the number of subclasses
 * to define and to describe the simulation architecture which would then
 * require the use of factories to create the instances of simulation engines.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-12-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			MoleneAtomicSimulatorPlugin
extends		AtomicSimulatorPlugin
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation() throws Exception
	{
		// Get the Mole model associated to the simulation engine of
		// this plug-in.
		ModelDescriptionI m =
				this.simulator.getDescendentModel(this.simulator.getURI()) ;
		// The model must be a Molene model implementing the disposePlotters
		// method, which is then called.
		assert	m instanceof MoleneModelImplementationI ;
		((MoleneModelImplementationI)m).disposePlotters() ;

		// Perform the rest of the finalisation operations.
		super.finaliseSimulation() ;
	}
}
// -----------------------------------------------------------------------------
