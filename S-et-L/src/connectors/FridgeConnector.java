package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FridgeI;
import utils.FridgeMode;

public class FridgeConnector extends AbstractConnector
implements FridgeI{


	@Override
	public FridgeMode getState() throws Exception {
		return ((FridgeI)this.offering).getState();
	}

	@Override
	public void turnOff() throws Exception {
		((FridgeI)this.offering).turnOff();
	}

	@Override
	public void turnOn() throws Exception {
		((FridgeI)this.offering).turnOn();
		
	}

	@Override
	public void setTemperatur(int temperature) throws Exception {
		((FridgeI)this.offering).setTemperatur(temperature);
		
	}

}
