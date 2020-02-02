package clean.equipments.fridge.unittest;

import clean.equipments.fridge.components.FridgeComponent;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

public class CVM 
extends AbstractCVM{
	
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
		AbstractComponent.createComponent(
				FridgeComponent.class.getCanonicalName(),
				new Object[]{FridgeComponent.SIL_STAND_ALONE}) ;

		super.deploy();
	}
	
	public static void	main(String[] args)
	{
		try {
			CVM c = new CVM() ;
			c.startStandardLifeCycle(75000L) ;
			Thread.sleep(10000) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
