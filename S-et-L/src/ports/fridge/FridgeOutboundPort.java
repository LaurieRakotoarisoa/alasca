package ports.fridge;

import components.compteur.Compteur;
import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.FridgeI;
import utils.fridge.FridgeMode;

public class FridgeOutboundPort extends AbstractOutboundPort
implements FridgeI{
	
	public FridgeOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,FridgeI.class, owner);
		
		assert owner instanceof EnergyController | owner instanceof Compteur;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public FridgeMode getState() throws Exception {
		return ((FridgeI)this.connector).getState();
	}


	@Override
	public void turnOff() throws Exception {
		((FridgeI)this.connector).turnOff();
		
	}


	@Override
	public void turnOn() throws Exception {
		((FridgeI)this.connector).turnOn();
		
	}


	@Override
	public void setTemperature(int temperature) throws Exception {
		((FridgeI)this.connector).setTemperature(temperature);
		
	}


	@Override
	public int getCons() throws Exception {
		return ((FridgeI)this.connector).getCons();
	}

}
