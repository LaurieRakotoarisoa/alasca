package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.CarBatteryI;
import utils.BatteryMode;

public class CarBatteryConnector extends AbstractConnector
implements CarBatteryI{

	@Override
	public int getLevelEnergy() throws Exception {
		return ((CarBatteryI)this.offering).getLevelEnergy();
	}

	@Override
	public BatteryMode getState() throws Exception {
		return ((CarBatteryI)this.offering).getState();
	}

}
