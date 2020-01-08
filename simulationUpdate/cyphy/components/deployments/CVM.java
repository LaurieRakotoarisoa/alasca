package simulation.cyphy.components.deployments;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.Controller.HomeController;
import simulation.cyphy.components.HomeControllerComponent;

public class CVM 
extends AbstractCVM{

	public CVM() throws Exception {
		super();
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
	}
	
	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		HashMap<String,String> hm = new HashMap<>() ;
		
		String hcURI =
				AbstractComponent.createComponent(
					HomeControllerComponent.class.getCanonicalName(),
					new Object[]{}) ;
			hm.put(HomeController.URI, hcURI) ;
		super.deploy();
	}
	
	public static void	main(String[] args)
	{
		try {
			CVM vm = new CVM() ;
			vm.startStandardLifeCycle(15000L) ;
			Thread.sleep(30000L) ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
