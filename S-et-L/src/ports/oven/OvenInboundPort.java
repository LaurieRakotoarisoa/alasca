package ports.oven;


import components.device.Oven;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.OvenI;
import utils.OvenMode;



/**
 *	The class <code>OvenInboundPort</code> defines the inbound port
 * exposing the interface <code>OvenI</code> for components of
 * type <code>Oven</code>.
 * 
 * @author Laurie Rakotoarisoa
 *
 */
public class OvenInboundPort  extends AbstractInboundPort
implements OvenI{

	public OvenInboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, OvenI.class, owner);
		assert owner instanceof Oven;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @see interfaces.OvenI#getState()
	 */
	@Override
	public OvenMode getState() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Oven)owner).getModeService());
	}


	@Override
	public void turnOff() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Oven)owner).setModeService(OvenMode.Off));
	}


	@Override
	public void turnOn() throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Oven)owner).setModeService(OvenMode.On));
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		this.getOwner().handleRequestSync(owner -> ((Oven)owner).setTemperatur(temperature));
		
	}

	@Override
	public void turnOn(int temperature) throws Exception {
		this.turnOn();
		this.setTemperature(temperature);
	}


	@Override
	public int getCons() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Oven)owner).getCon());
	}

}
