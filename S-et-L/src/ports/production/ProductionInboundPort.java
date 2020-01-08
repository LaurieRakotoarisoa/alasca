package ports.production;

import components.production.Battery;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.ProductionI;

/**
 *	The class <code>ProductionInboundPort</code> defines the inbound port
 * exposing the interface <code>ProductionI</code> for components of
 * type <code>Production</code>.
 * 
 * @author Saad CHIADMI
 *
 */
public class ProductionInboundPort extends AbstractInboundPort
implements ProductionI{

	public ProductionInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, ProductionI.class, owner);
		assert owner instanceof Battery;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getProduction() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Battery)owner).getProduction());
	}

	@Override
	public void setProduction(int production) throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Battery)owner).setProduction(production));
	}
}
