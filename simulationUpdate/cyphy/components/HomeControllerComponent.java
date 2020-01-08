package simulation.cyphy.components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import simulation.Controller.HomeController;

public class HomeControllerComponent 
extends AbstractCyPhyComponent{
	
	protected final String pluginURI;

	protected HomeControllerComponent() throws Exception {
		super(1,0);
		this.pluginURI = HomeController.URI;
		this.initialise();

	}
	
	protected void		initialise() throws Exception
	{
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		this.asp = new AtomicSimulatorPlugin() ;
		this.asp.setPluginURI(this.pluginURI) ;
		this.asp.setSimulationArchitecture(localArchitecture) ;
		this.installPlugin(this.asp) ;
		this.toggleLogging();

	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		
		return HomeController.build();
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.logMessage("HomeController component " + this.pluginURI
													+ " begins execution.") ;
	}
	
	

}
