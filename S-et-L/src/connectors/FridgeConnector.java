package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FridgeI;
import utils.FridgeMode;

public class FridgeConnector extends AbstractConnector
implements FridgeI{


	@Override
	public FridgeMode getState() throws Exception {
		return ((FridgeI)this.offering).getState();
	}

}
