package connectors;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.OvenI;
import utils.OvenMode;

public class OvenConnector extends AbstractConnector
implements OvenI{


	@Override
	public OvenMode getState() throws Exception {
		return ((OvenI)this.offering).getState();
	}

	@Override
	public void turnOff() throws Exception {
		((OvenI)this.offering).turnOff();
	}

	@Override
	public void turnOn() throws Exception {
		((OvenI)this.offering).turnOn();
	}

	@Override
	public void turnOn(int temperature) throws Exception {
		((OvenI)this.offering).turnOn(temperature);
		
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		((OvenI)this.offering).setTemperature(temperature);
	}

	@Override
	public int getCons() throws Exception {
		return ((OvenI)this.offering).getCons();
	}

}
