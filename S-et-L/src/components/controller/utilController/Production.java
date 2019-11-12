package components.controller.utilController;

import components.controller.EnergyController;
import ports.production.ProductionOutboundPort;

public class Production {
	
	public static int getProduction(ProductionOutboundPort productionOutbound, 
					EnergyController controller) throws Exception{
		
		return productionOutbound.getProduction();
	}
	
	public static void setProduction(int cons,
					ProductionOutboundPort productionOutbound, 
					EnergyController controller) throws Exception{
		
		int oldProduction = getProduction(productionOutbound, controller);
		int newProduction = oldProduction - cons;
		productionOutbound.setProduction(newProduction);
		controller.logMessage("Production : "+newProduction);
		checkProduction(newProduction, productionOutbound, controller);
	}
	
	public static void checkProduction( int production,
			ProductionOutboundPort productionOutbound, 
			EnergyController controller) throws Exception{
		if(production<10000) {
			
		}
	}

}
