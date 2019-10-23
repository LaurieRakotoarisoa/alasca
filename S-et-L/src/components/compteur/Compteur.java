package components.compteur;

import java.util.concurrent.TimeUnit;

import components.controller.EnergyController;
import components.controller.utilController.Fridge;
import components.controller.utilController.Oven;
import components.controller.utilController.TV;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.CompteurI;
import interfaces.FridgeI;
import interfaces.OvenI;
import interfaces.TVI;
import ports.fridge.FridgeOutboundPort;
import ports.oven.OvenOutboundPort;
import ports.tv.TVOutboundPort;

/**
 * The class <code>Compteur</code> implements a component that models a Compteur's behavior
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = CompteurI.class)
@RequiredInterfaces (required = {OvenI.class, TVI.class, FridgeI.class})
public class Compteur extends AbstractComponent{
	
	
	protected OvenOutboundPort ovenOutbound;
	protected FridgeOutboundPort fridgeOutbound;
	protected TVOutboundPort tvOutbound;
	
	protected Compteur(String URI,String ovenOutboundURI, String TVOutboundURI, String FridgeOutboundURI) throws Exception {
		super(URI,1,1 );
		
		ovenOutbound = new OvenOutboundPort(ovenOutboundURI,this);
		ovenOutbound.localPublishPort();
		tvOutbound = new TVOutboundPort(TVOutboundURI,this);
		tvOutbound.localPublishPort();
		fridgeOutbound = new FridgeOutboundPort(FridgeOutboundURI,this);
		fridgeOutbound.localPublishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("energy counter") ;
	}
	
	/**
	 * <p>Give information about the current consumption of all devices</p>
	 * @return {@link integer}
	 * @throws Exception 
	 */
	public int getCons() throws Exception {
		int cons = Oven.getCons(ovenOutbound, this)+Fridge.getCons(fridgeOutbound, this)+TV.getCons(tvOutbound, this);
		this.logMessage("La consomation d'énergie acctuel est "+cons+" Watt");
		return cons;
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
		// This is the place where to clean up resources, such as
		// disconnecting and unpublishing ports that will be destroyed
		// when shutting down.
		// In static architectures like in this example, ports can also
		// be disconnected by the finalise method of the component
		// virtual machine.
		this.ovenOutbound.doDisconnection();
		this.ovenOutbound.unpublishPort() ;
		this.fridgeOutbound.doDisconnection();
		this.fridgeOutbound.unpublishPort() ;
		this.tvOutbound.doDisconnection();
		this.tvOutbound.unpublishPort() ;
//		this.counterOutbound.doDisconnection();
//		this.counterOutbound.unpublishPort() ;

		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}

}
