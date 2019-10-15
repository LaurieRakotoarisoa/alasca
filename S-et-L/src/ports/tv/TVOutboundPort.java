package ports.tv;

import components.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.TVI;
import utils.TVMode;

public class TVOutboundPort extends AbstractOutboundPort
implements TVI{
	
	public TVOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,TVI.class, owner);
		
		assert owner instanceof EnergyController;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public TVMode getState() throws Exception {
		return ((TVI)this.connector).getState();
	}

}
