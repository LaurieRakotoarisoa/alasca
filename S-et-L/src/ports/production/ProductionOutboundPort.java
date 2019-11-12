package ports.production;

import components.compteur.Compteur;
import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ProductionI;

public class ProductionOutboundPort extends AbstractOutboundPort
implements ProductionI {
	
	public ProductionOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,ProductionI.class, owner);
		
		assert owner instanceof EnergyController | owner instanceof Compteur;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getProduction() throws Exception {
		return ((ProductionI)this.connector).getProduction();
	}

	@Override
	public void setProduction(int production) throws Exception {
		((ProductionI)this.connector).setProduction(production);
	}

}
