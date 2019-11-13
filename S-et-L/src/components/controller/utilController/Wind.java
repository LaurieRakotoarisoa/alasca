package components.controller.utilController;

import components.controller.EnergyController;
import ports.turbine.TurbineOutboundPort;
import utils.TurbineMode;

public class Wind {
	
	public static void getMode(TurbineOutboundPort windOutbound, 
					EnergyController controller) throws Exception{
		TurbineMode m = windOutbound.getState();
		controller.logMessage("Etat du éolienne : "+m);
		
	}
	
	public static void turnOn(TurbineOutboundPort windOutbound, 
					EnergyController controller) throws Exception{
		windOutbound.turnOn();
		controller.logMessage("Allumer l'éolienne : ON");
		
	}

}
