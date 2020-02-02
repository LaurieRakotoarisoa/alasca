package ports.oven;


import components.compteur.Compteur;
import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.OvenI;
import utils.oven.OvenLightMode;
import utils.oven.OvenMode;

public class OvenOutboundPort extends AbstractOutboundPort
implements OvenI{

	public OvenOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,OvenI.class, owner);
		
		assert owner instanceof EnergyController | owner instanceof Compteur;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public OvenMode getState() throws Exception {
		return ((OvenI)this.connector).getState();
	}


	@Override
	public void turnOff() throws Exception {
		((OvenI)this.connector).turnOff();
	}


	@Override
	public void turnOn() throws Exception {
		((OvenI)this.connector).turnOn();
	}


	@Override
	public void turnOn(int temperature) throws Exception {
		((OvenI)this.connector).turnOn(temperature);
	}


	@Override
	public void setTemperature(int temperature) throws Exception {
		((OvenI)this.connector).setTemperature(temperature);
	}
	
	@Override
	public void activateEcoMode() throws Exception {
		((OvenI)this.connector).activateEcoMode();
		
	}


	@Override
	public void deactivateEcoMode() throws Exception {
		((OvenI)this.connector).deactivateEcoMode();
		
	}

	@Override
	public int getCons() throws Exception {
		return ((OvenI)this.connector).getCons();
	}


	@Override
	public void setModeLight(OvenLightMode mode) throws Exception {
		((OvenI)this.connector).setModeLight(mode);
		
	}


	@Override
	public void forbidPyrolysis() throws Exception {
		((OvenI)this.connector).forbidPyrolysis();
		
	}


	@Override
	public void allowPyrolysis() throws Exception {
		((OvenI)this.connector).allowPyrolysis();
		
	}

}
