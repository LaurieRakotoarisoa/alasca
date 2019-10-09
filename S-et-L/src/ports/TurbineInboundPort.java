package ports;

import components.WindTurbine;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.WindTurbineI;
import utils.TurbineMode;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int maxPower() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpeed() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
