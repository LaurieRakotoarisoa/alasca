package ports;

import components.CarBattery;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.CarBatteryI;
import utils.BatteryMode;



/**
 *	The class <code>CarBatteryInboundPort</code> defines the inbound port
 * exposing the interface <code>CarBatteryI</code> for components of
 * type <code>CarBattery</code>.
 * 
 * @author Laurie Rakotoarisoa
 *
 */
public class CarBatteryInboundPort  extends AbstractInboundPort
implements CarBatteryI{

	public CarBatteryInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, CarBatteryI.class, owner);
		assert owner instanceof CarBattery;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/** 
	 * @see interfaces.CarBatteryI#getLevelEnergy()
	 */
	@Override
	public int getLevelEnergy() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((CarBattery)owner).getLevelEnergyService());
	}


	/**
	 * @see interfaces.CarBatteryI#getState()
	 */
	@Override
	public BatteryMode getState() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((CarBattery)owner).getModeService());
	}

}
