package fr.sorbonne_u.components.cyphy.examples.hem.deployments;

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
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.examples.hem.components.CoordinatorComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.components.SGSupervisorComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.components.EnergyManager;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.mil.models.EnergyManagerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.sil.models.EnergyManagerSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.components.ElectricMeter;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.models.ElectricMeterMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.sil.models.ElectricMeterSILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SGCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

//------------------------------------------------------------------------------
/**
 * The class <code>CVM</code> implements a single JVM deployment for the
 * household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVM
extends		AbstractCVM
{
	/**
	 * The enumeration <code>SimulationMode</code> defines three possible mode
	 * of execution of this component assembly.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2020-01-23</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected enum		SimulationMode
	{
		NO_SIMULATION,		// no simulation, running the functional code only.
		MIL_SIMULATION,		// model-in-the-loop simulation running the
							// simulation code only.
		SIL_SIMULATION		// software-in-the-loop simulation running the
							// simulation code and the functional code.
	}

	/** the execution mode selected by changing the value of this constant.	*/
	protected static final SimulationMode SIM_MODE =
												SimulationMode.SIL_SIMULATION ;

	public				CVM() throws Exception
	{
		super() ;
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		if (SIM_MODE == SimulationMode.NO_SIMULATION) {

			// With no simulation
			String electricMeterComponentURI =
				AbstractComponent.createComponent(
						ElectricMeter.class.getCanonicalName(),
						new Object[]{SimulationArchitectures.NONE}) ;
			AbstractComponent.createComponent(
					EnergyManager.class.getCanonicalName(),
					new Object[]{electricMeterComponentURI,
								 SimulationArchitectures.NONE}) ;
			AbstractComponent.createComponent(
					HairDryer.class.getCanonicalName(),
					new Object[]{SimulationArchitectures.NONE}) ;

		} else if (SIM_MODE == SimulationMode.MIL_SIMULATION) {

			// With MIL simulation
			HashMap<String,String> hm = new HashMap<>() ;
			String electricMeterComponentURI =
					AbstractComponent.createComponent(
							ElectricMeter.class.getCanonicalName(),
							new Object[]{SimulationArchitectures.MIL}) ;
			hm.put(ElectricMeterMILModel.URI, electricMeterComponentURI) ;
			String energyManagerComponentURI =
					AbstractComponent.createComponent(
							EnergyManager.class.getCanonicalName(),
							new Object[]{electricMeterComponentURI,
										 SimulationArchitectures.MIL}) ;
			hm.put(EnergyManagerMILModel.URI, energyManagerComponentURI) ;
			String hairDryerComponentURI =
				AbstractComponent.createComponent(
					HairDryer.class.getCanonicalName(),
					new Object[]{SimulationArchitectures.MIL}) ;
			hm.put(HairDryerMILCoupledModel.URI, hairDryerComponentURI) ;
			String coordinatorURI =
				AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[]{}) ;
			hm.put(SGCoupledModel.URI, coordinatorURI) ;
			AbstractComponent.createComponent(
					SGSupervisorComponent.class.getCanonicalName(),
					new Object[]{SimulationArchitectures.MIL, hm}) ;

		} else if (SIM_MODE == SimulationMode.SIL_SIMULATION) {

			// With SIL simulation
			HashMap<String,String> hm = new HashMap<>() ;
			String electricMeterComponentURI =
					AbstractComponent.createComponent(
							ElectricMeter.class.getCanonicalName(),
							new Object[]{SimulationArchitectures.SIL}) ;
			hm.put(ElectricMeterSILModel.URI, electricMeterComponentURI) ;
			String energyManagerComponentURI =
					AbstractComponent.createComponent(
							EnergyManager.class.getCanonicalName(),
							new Object[]{electricMeterComponentURI,
										 SimulationArchitectures.SIL}) ;
			hm.put(EnergyManagerSILModel.URI, energyManagerComponentURI) ;
			String hairDryerComponentURI =
					AbstractComponent.createComponent(
							HairDryer.class.getCanonicalName(),
							new Object[]{SimulationArchitectures.SIL}) ;
			hm.put(HairDryerSILCoupledModel.URI, hairDryerComponentURI) ;
			String coordinatorURI =
				AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[]{}) ;
			hm.put(SGCoupledModel.URI, coordinatorURI) ;
			AbstractComponent.createComponent(
					SGSupervisorComponent.class.getCanonicalName(),
					new Object[]{SimulationArchitectures.SIL, hm}) ;

		}

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVM c = new CVM() ;
			c.startStandardLifeCycle(
					SIM_MODE == SimulationMode.NO_SIMULATION ?
						10000L
					:	SIM_MODE == SimulationMode.MIL_SIMULATION ?
							20000L
						:	75000L) ;
			Thread.sleep(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
//------------------------------------------------------------------------------
