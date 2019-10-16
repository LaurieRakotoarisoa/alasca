package components.controller.utilController;

import components.controller.EnergyController;
import ports.tv.TVOutboundPort;
import utils.TVMode;

public class TV {
	
	public static void TurnOff(TVOutboundPort tvOutbound, 
						EnergyController controller) throws Exception{
		tvOutbound.turnOff();
		controller.logMessage("Etat de la télé : Off");
		
	}
	
	public static void TurnOn(TVOutboundPort tvOutbound, 
						EnergyController controller) throws Exception{
		tvOutbound.turnOn();
		controller.logMessage("Etat de la télé : ON");
		
	}
	
	public static void SetBacklight(TVOutboundPort tvOutbound, 
						EnergyController controller, 
						int backlight) throws Exception{
		tvOutbound.setBacklight(backlight);
		controller.logMessage("Modification du retro-éclairage de la télé :"+backlight);
		
	}
	
	public static void getMode(TVOutboundPort tvOutbound, 
						EnergyController controller) throws Exception{
		TVMode m = tvOutbound.getState();
		controller.logMessage("Etat de la télé : "+m);
		
	}

}
