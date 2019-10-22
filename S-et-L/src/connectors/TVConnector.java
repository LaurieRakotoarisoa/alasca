package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.TVI;
import utils.TVMode;

public class TVConnector extends AbstractConnector
implements TVI{


	@Override
	public TVMode getState() throws Exception {
		return ((TVI)this.offering).getState();
	}

	@Override
	public void turnOff() throws Exception {
		((TVI)this.offering).turnOff();
		
	}

	@Override
	public void turnOn() throws Exception {
		((TVI)this.offering).turnOn();
		
	}

	@Override
	public void setBacklight(int backlight) throws Exception {
		((TVI)this.offering).setBacklight(backlight);
		
	}

	@Override
	public int getCons() throws Exception {
		return ((TVI)this.offering).getCons();
	}

}
