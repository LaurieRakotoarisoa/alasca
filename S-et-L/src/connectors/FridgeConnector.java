package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.FridgeI;
import utils.fridge.FridgeMode;

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
	public void setTemperature(double temperature) throws Exception {
		((FridgeI)this.offering).setTemperature(temperature);
		
	}

	@Override
	public double getCons() throws Exception {
		return ((FridgeI)this.offering).getCons();
	}

	@Override
	public void activateEcoMode() throws Exception {
		((FridgeI)this.offering).activateEcoMode();
		
	}

	@Override
	public void deactivateEcoMode() throws Exception {
		((FridgeI)this.offering).deactivateEcoMode();
		
	}

	@Override
	public void openDoor() throws Exception {
		((FridgeI)this.offering).openDoor();
		
	}

	@Override
	public void closeDoor() throws Exception {
		((FridgeI)this.offering).closeDoor();
		
	}

}
