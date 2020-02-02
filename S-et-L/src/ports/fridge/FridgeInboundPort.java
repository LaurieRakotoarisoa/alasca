package ports.fridge;

import clean.equipments.fridge.components.FridgeComponent;
import components.device.Fridge;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.FridgeI;
import utils.fridge.FridgeMode;

/**
 *	The class <code>FridgeInboudPort</code> defines the inbound port
 * exposing the interface <code>FridgeI</code> for components of
 * type <code>Fridge</code>.
 * 
 * @author Saad CHIADMI
 *
 */
public class FridgeInboundPort extends AbstractInboundPort
implements FridgeI{
	
	public FridgeInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, FridgeI.class, owner);
		assert owner instanceof Fridge || owner instanceof FridgeComponent;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *@see interfaces.TVI#getState()
	 */
	@Override
	public FridgeMode getState() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Fridge)owner).getModeService());
	}

	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).setModeService(FridgeMode.Off_Close));
		
	}

	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).setModeService(FridgeMode.On_Close));
		
	}

	@Override
	public void setTemperature(double temperature) throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).setTemperature(temperature));
		
	}

	@Override
	public double getCons() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Fridge)owner).getCons());
	}

	@Override
	public void activateEcoMode() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).setEcoMode(true));
		
	}

	@Override
	public void deactivateEcoMode() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).setEcoMode(false));
		
	}

	@Override
	public void openDoor() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).openDoor());
		
	}

	@Override
	public void closeDoor() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Fridge)owner).closeDoor());
		
	}

}
