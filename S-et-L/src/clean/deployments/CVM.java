package clean.deployments;

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

public class CVM 
extends AbstractCVM{
	
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

		} else if (SIM_MODE == SimulationMode.MIL_SIMULATION) {

		} else if (SIM_MODE == SimulationMode.SIL_SIMULATION) {

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
