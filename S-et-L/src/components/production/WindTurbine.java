package components.production;

import fr.sorbonne_u.components.AbstractComponent;
import ports.TurbineInboundPort;
import utils.TurbineMode;

public class WindTurbine extends AbstractComponent{
	
	protected TurbineMode state = TurbineMode.OFF;
	protected TurbineInboundPort tb;

	protected WindTurbine(String uri, String inboundURI) throws Exception {
		super(uri,1, 0);
		
		tb = new TurbineInboundPort(inboundURI, this);
		tb.publishPort();
	}

}
