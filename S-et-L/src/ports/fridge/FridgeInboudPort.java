package ports.fridge;

import components.Fridge;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FridgeI;
import utils.FridgeMode;

/**
 *	The class <code>FridgeInboudPort</code> defines the inbound port
 * exposing the interface <code>TVI</code> for components of
 * type <code>TV</code>.
 * 
 * @author Saad CHIADMI
 *
 */
public class FridgeInboudPort extends AbstractInboundPort
implements FridgeI{
	
	public FridgeInboudPort(String uri,ComponentI owner) throws Exception {
		super(uri, FridgeI.class, owner);
		assert owner instanceof Fridge;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *@see interfaces.TVI#getState()
	 */
	@Override
	public FridgeMode getState() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Fridge)owner).getModeService());
	}

}