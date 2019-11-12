package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ProductionI;

public class ProductionConnector extends AbstractConnector
implements ProductionI {

	@Override
	public int getProduction() throws Exception {
		return ((ProductionI)this.offering).getProduction();
	}

	@Override
	public void setProduction(int production) throws Exception {
		((ProductionI)this.offering).setProduction(production);
	}

}
