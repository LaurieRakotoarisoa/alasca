package ports.turbine;

import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.WindTurbineI;
import utils.TurbineMode;

public class TurbineOutboundPort extends AbstractOutboundPort
implements WindTurbineI{

	public TurbineOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,WindTurbineI.class, owner);
		assert owner instanceof EnergyController;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public TurbineMode getState() throws Exception {
		return ((WindTurbineI)this.connector).getState();
	}

	@Override
	public int getProduction() throws Exception {
		// TODO Auto-generated method stub
		return ((WindTurbineI)this.connector).getProduction();
	}

	@Override
	public void turnOff() throws Exception {
		((WindTurbineI)this.connector).turnOff();
	}

	@Override
	public void turnOn() throws Exception {
		((WindTurbineI)this.connector).turnOn();
	}

}
