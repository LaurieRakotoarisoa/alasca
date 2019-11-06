package components.production;

import fr.sorbonne_u.components.AbstractComponent;
import turbine.TurbineInboundPort;
import utils.TurbineMode;

public class WindTurbine extends AbstractComponent{
	
	protected TurbineMode state = TurbineMode.OFF;
	protected int production =0;


	protected WindTurbine(String uri, String inboundURI) throws Exception {
		super(uri,1, 0);
	}
	
	public TurbineMode getState() throws Exception{
		return state;
	}
	
	public int maxPower() {
		return 500;
	}
	
	public int getProduction() {
		return production;
	}

}
