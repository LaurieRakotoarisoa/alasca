package ports.tv;

import components.device.TV;
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
public class TVInboundPort extends AbstractInboundPort
implements TVI{
	
	public TVInboundPort(String uri,ComponentI owner) throws Exception {
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

	/**
	 * @see interfaces.TVI#turnOff()
	 */
	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((TV) owner).setModeService(TVMode.Off));
		
	}

	/**
	 * @see interfaces.TVI#turnOn()
	 */
	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((TV) owner).setModeService(TVMode.On));
	}

	@Override
	public void setBacklight(int backlight) throws Exception {
		this.getOwner().handleRequestSync(owner -> ((TV) owner).setBacklight(backlight));
		
	}

	@Override
	public int getCons() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((TV)owner).getCons());
	}

}
