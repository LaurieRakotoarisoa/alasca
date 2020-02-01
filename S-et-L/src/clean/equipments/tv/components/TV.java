package clean.equipments.tv.components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.TVI;
import utils.TVMode;

public class TV 
extends AbstractCyPhyComponent
implements TVI,
EmbeddingComponentAccessI{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected TV(String simArchitectureURI) {
		super(3, 0);
		assert	simArchitectureURI != null ;
	}
	
	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	
	private TVMode currentMode;
	
	private double currentCons;
	
	private double backlight;
	
	private double lastBacklight;
	
	private boolean ecoMode;
	

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public TVMode getState() throws Exception {
		return currentMode;
	}

	@Override
	public double getCons() throws Exception {
		return currentCons;
	}

	@Override
	public void turnOff() throws Exception {
		assert currentMode == TVMode.On && backlight != 0;
		currentMode = TVMode.Off;
		lastBacklight = backlight;
		backlight = 0.0;
		
		
	}

	@Override
	public void turnOn() throws Exception {
		assert currentMode == TVMode.Off && backlight == 0;
		currentMode = TVMode.On;
		backlight = lastBacklight;
		this.logMessage("TV has been turned on");
		
	}

	@Override
	public void setBacklight(int backlight) throws Exception {
		this.backlight = backlight;
		this.logMessage("TV backlight has been set to "+this.backlight);
		
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

}
