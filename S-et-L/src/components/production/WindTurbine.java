package components.production;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.WindTurbineI;
import ports.turbine.TurbineInboundPort;
import utils.TurbineMode;

@OfferedInterfaces (offered = WindTurbineI.class)
public class WindTurbine extends AbstractComponent{
	
	protected TurbineMode state = TurbineMode.OFF;
	protected int production =0;


	protected WindTurbine(String uri, String inboundURI) throws Exception {
		super(uri,1, 0);
		
		//Create and publish port for remote control
		PortI WindInboundPort = new TurbineInboundPort(inboundURI,this);
		WindInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home"));
		this.tracer.setTitle("Wind Turbine");
	}
	
	public TurbineMode getState() throws Exception{
		return state;
	}
	
	public TurbineMode setMode(TurbineMode mode) {
		state = mode;
		if(mode == TurbineMode.ON) {
			//set production +100 (while true)
		}
		this.logMessage("Wind Turbinel state "+ mode);
		return mode;
	}
	
	public int getProduction() {
		return production;
	}

}
