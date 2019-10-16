package ports.carBattery;

import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.CarBatteryI;
import utils.BatteryMode;

public class CarBatteryOutboundPort extends AbstractOutboundPort
implements CarBatteryI{

	public CarBatteryOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,CarBatteryI.class, owner);
		
		assert owner instanceof EnergyController;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getLevelEnergy() throws Exception {
		return ((CarBatteryI)this.connector).getLevelEnergy();
	}

	@Override
	public BatteryMode getState() throws Exception {
		return ((CarBatteryI)this.connector).getState();
	}

}
