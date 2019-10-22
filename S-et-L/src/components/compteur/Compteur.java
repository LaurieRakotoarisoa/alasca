package components.compteur;

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
		int cons =0; //Oven.getCons(ovenOutbound, this)+Fridge.getCons(fridgeOutbound, this)+TV.getCons(tvOutbound, this);
		this.logMessage("La consomation d'énergie acctuel est "+cons+" Watt");
		return cons;
	}

}
