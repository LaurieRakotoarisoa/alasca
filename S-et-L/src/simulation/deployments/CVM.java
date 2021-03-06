package simulation.deployments;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.TV.components.TV;

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
		@SuppressWarnings("unused")
		String componentURI =
				AbstractComponent.createComponent(
						TV.class.getCanonicalName(),
						new Object[]{}) ;
		this.toggleLogging(componentURI);
		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVM c = new CVM() ;
			c.startStandardLifeCycle(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
