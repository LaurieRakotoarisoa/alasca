package components.production;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.ProductionI;
import ports.production.ProductionInboundPort;

@OfferedInterfaces (offered = ProductionI.class)
public class Production extends AbstractComponent{
	
	protected int production =1000000;
	
	protected Production(String uri, String inboundURI) throws Exception {
		super(uri,1, 0);
		
		//Create and publish port for remote control
		PortI ProductInboundPort = new ProductionInboundPort(inboundURI,this);
		ProductInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home"));
		this.tracer.setTitle("Production");
	}
	
	/**
	 * <p>Give information about the production cons of the Production</p>
	 * @return {@link Integer}
	 */
	public int getProduction() {
		return production;
	}
	
	/**
	 * <p>Update information about the production cons of the Production</p>
	 * @return {@link Integer}
	 */
	public int setProduction(int production) {
		this.production = production;
		this.logMessage("Production "+ production);
		return production;
	}
}
