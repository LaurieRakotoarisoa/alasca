package ports;

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
	public int maxPower() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProduction() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
