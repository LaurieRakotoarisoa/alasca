package ports.turbine;

import components.production.WindTurbine;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.WindTurbineI;
import utils.TurbineMode;

/**
 * @author Laurie Rakotoarisoa
 *
 */
public class TurbineInboundPort extends AbstractInboundPort
implements WindTurbineI{

	public TurbineInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, WindTurbineI.class, owner);
		
		assert owner instanceof WindTurbine;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public TurbineMode getState() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WindTurbine)owner).getState());
	}

	@Override
	public int getProduction() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WindTurbine)owner).getProduction());
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((WindTurbine)owner).setMode(TurbineMode.OFF));
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((WindTurbine)owner).setMode(TurbineMode.ON));
	}


	

}
