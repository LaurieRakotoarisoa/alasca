package clean.equipments.oven.components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.OvenI;
import utils.oven.OvenLightMode;
import utils.oven.OvenMode;

public class OvenComponent 
extends AbstractCyPhyComponent
implements OvenI,
EmbeddingComponentAccessI{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected OvenComponent(String simArchitectureURI) {
		super(3, 0);
		assert	simArchitectureURI != null ;
	}
	
	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	
	private OvenMode currentMode;
	
	private double currentCons;
	
	private double temperature;
	
	private double lastTemperature;
	
	private OvenLightMode lightMode;
	
	private boolean ecoMode;
	

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public OvenMode getState() throws Exception {
		return currentMode;
	}

	@Override
	public int getCons() throws Exception {
		return (int) currentCons;
	}

	@Override
	public void turnOff() throws Exception {
		assert currentMode == OvenMode.On && temperature != 0;
		currentMode = OvenMode.Off;
		lastTemperature = temperature;
		temperature = 0.0;
		
		
	}

	@Override
	public void turnOn() throws Exception {
		assert currentMode == OvenMode.Off && temperature == 0;
		currentMode = OvenMode.On;
		temperature = lastTemperature;
		this.logMessage("Oven has been turned on");
		
	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		this.temperature = temperature;
		this.logMessage("Oven temperature has been set to "+this.temperature);
		
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activateEcoMode() throws Exception {
		assert !ecoMode;
		ecoMode = true;
		
	}

	@Override
	public void deactivateEcoMode() throws Exception {
		assert ecoMode;
		ecoMode = false;
		
	}

	@Override
	public void turnOn(int temperature) throws Exception {
		assert currentMode == OvenMode.Off && temperature == 0;
		currentMode = OvenMode.On;
		this.temperature = temperature;
		this.logMessage("Oven has been turned on");
		this.logMessage("Oven temperature has been set to "+this.temperature);
	}

	@Override
	public void setModeLight(OvenLightMode lightMode) throws Exception {
		this.lightMode = lightMode;
	}

	@Override
	public void forbidPyrolysis() throws Exception {}

	@Override
	public void allowPyrolysis() throws Exception {}

}
