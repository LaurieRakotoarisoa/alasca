package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.CompteurI;

public class CompteurConnector extends AbstractConnector
implements CompteurI{

	@Override
	public int getConsumptionOfAllDevices() throws Exception {
		return ((CompteurI)this.offering).getConsumptionOfAllDevices();
	}

}
