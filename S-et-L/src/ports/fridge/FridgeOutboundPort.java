package ports.fridge;

import components.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.FridgeI;
import utils.FridgeMode;

public class FridgeOutboundPort extends AbstractOutboundPort
implements FridgeI{
	
	public FridgeOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,FridgeI.class, owner);
		
		assert owner instanceof EnergyController;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public FridgeMode getState() throws Exception {
		return ((FridgeI)this.connector).getState();
	}

}