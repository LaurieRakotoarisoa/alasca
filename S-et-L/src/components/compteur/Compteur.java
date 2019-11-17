package components.compteur;

import java.util.concurrent.TimeUnit;

import components.controller.EnergyController;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.CompteurI;
import interfaces.FridgeI;
import interfaces.OvenI;
import interfaces.TVI;
import ports.compteur.CompteurInboudPort;

/**
 * The class <code>Compteur</code> implements a component that models a Compteur's behavior
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = CompteurI.class)
@RequiredInterfaces (required = {OvenI.class, TVI.class, FridgeI.class})
public class Compteur extends AbstractComponent{
		
	protected Compteur(String URI,String inboundURI) throws Exception {
		super(URI,1,1 );
		PortI counterInbound = new CompteurInboudPort(inboundURI, this);
		counterInbound.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("energy counter") ;
	}
	
	/**
	 * <p>Give information about the current consumption of all devices</p>
	 * @return {@link integer}
	 * @throws Exception 
	 */
	public int getCons() throws Exception {
		//Consommation totale en attendant la modélisation EVS
		return 12;
	}
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		this.logMessage("executing controller component.") ;
		// Schedule the first service method invocation in one second.
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						while(true) {
							((EnergyController)this.getTaskOwner()).getAllCons();
							Thread.sleep(1000);
						}
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping counter component.") ;
		this.printExecutionLogOnFile("controller");
		super.finalise();
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(CompteurI.class) ;
			p[0].unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
