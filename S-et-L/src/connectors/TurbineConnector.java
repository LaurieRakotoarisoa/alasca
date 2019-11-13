package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.WindTurbineI;
import utils.TurbineMode;

public class TurbineConnector extends AbstractConnector
implements WindTurbineI{

	@Override
	public TurbineMode getState() throws Exception {
		return ((WindTurbineI)this.offering).getState();
	}

	@Override
	public int getProduction() throws Exception {
		return ((WindTurbineI)this.offering).getProduction();
	}

	@Override
	public void turnOff() throws Exception {
		((WindTurbineI)this.offering).turnOff();
	}

	@Override
	public void turnOn() throws Exception {
		((WindTurbineI)this.offering).turnOn();
	}


}
