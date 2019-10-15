package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.TVI;
import utils.TVMode;

public class TVConnector extends AbstractConnector
implements TVI{


	@Override
	public TVMode getState() throws Exception {
		return ((TVI)this.offering).getState();
	}

}
