package ports.oven;


import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.OvenI;
import utils.OvenMode;

public class OvenOutboundPort extends AbstractOutboundPort
implements OvenI{

	public OvenOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,OvenI.class, owner);
		
		assert owner instanceof EnergyController;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public OvenMode getState() throws Exception {
		return ((OvenI)this.connector).getState();
	}


	@Override
	public void turnOff() throws Exception {
		((OvenI)this.connector).turnOff();
	}


	@Override
	public void turnOn() throws Exception {
		((OvenI)this.connector).turnOn();
	}


	@Override
	public void turnOn(int temperature) throws Exception {
		((OvenI)this.connector).turnOn(temperature);
	}


	@Override
	public void setTemperature(int temperature) throws Exception {
		((OvenI)this.connector).setTemperature(temperature);
	}

}
