package ports.tv;

import components.TV;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.TVI;
import utils.TVMode;

/**
 *	The class <code>TVInboundPort</code> defines the inbound port
 * exposing the interface <code>TVI</code> for components of
 * type <code>TV</code>.
 * 
 * @author Saad CHIADMI
 *
 */
public class TVInboudPort extends AbstractInboundPort
implements TVI{
	
	public TVInboudPort(String uri,ComponentI owner) throws Exception {
		super(uri, TVI.class, owner);
		assert owner instanceof TV;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *@see interfaces.TVI#getState()
	 */
	@Override
	public TVMode getState() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((TV)owner).getModeService());
	}

}
