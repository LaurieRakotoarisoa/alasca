package components.controller.utilController;

import components.compteur.Compteur;
import components.controller.EnergyController;
import ports.tv.TVOutboundPort;
import utils.TVMode;

public class TV {
	
	public static double getCons(TVOutboundPort tvOutbound,
			EnergyController counter) throws Exception{
		double cons = tvOutbound.getCons();
		counter.logMessage("Consomation de la télé : "+cons);
		return cons;
	}
	
	public static double getCons(TVOutboundPort tvOutbound,
			Compteur counter) throws Exception{
		double cons = tvOutbound.getCons();
		counter.logMessage("Consomation de la télé : "+cons);
		return cons;
	}
	
	public static void turnOff(TVOutboundPort tvOutbound, 
						EnergyController controller) throws Exception{
		tvOutbound.turnOff();
		controller.logMessage("Etat de la télé : Off");
		
	}
	
	public static void turnOn(TVOutboundPort tvOutbound, 
						EnergyController controller) throws Exception{
		tvOutbound.turnOn();
		controller.logMessage("Etat de la télé : ON");
		
	}
	
	public static void setBacklight(TVOutboundPort tvOutbound, 
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
	
	public static void activateEcoMode(TVOutboundPort tvOutbound, 
							EnergyController controller) throws Exception{
		tvOutbound.activateEcoMode();
		controller.logMessage("Mode économie activé pour la télé");
	}
	
	public static void deactivateEcoMode(TVOutboundPort tvOutbound, 
			EnergyController controller) throws Exception{
	tvOutbound.deactivateEcoMode();
	controller.logMessage("Mode économie désactivé pour la télé");
	}

}
