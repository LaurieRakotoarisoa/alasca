package components.controller.utilController;

import components.compteur.Compteur;
import components.controller.EnergyController;
import ports.fridge.FridgeOutboundPort;
import utils.fridge.FridgeMode;

public class Fridge {
	//controller
	public static double getCons(FridgeOutboundPort fridgeOutbound, 
			EnergyController counter) throws Exception{
		double cons = fridgeOutbound.getCons();
		counter.logMessage("Consomation du réfrigérateur : "+cons);
		return cons;
	}
	//counter
	public static double getCons(FridgeOutboundPort fridgeOutbound, 
			Compteur counter) throws Exception{
		double cons = fridgeOutbound.getCons();
		counter.logMessage("Consomation du réfrigérateur : "+cons);
		return cons;
	}
	
	public static void turnOff(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller) throws Exception{
		fridgeOutbound.turnOff();
		controller.logMessage("Etat du réfrigérateur : Off");
		
	}
	
	public static void turnOn(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller) throws Exception{
		fridgeOutbound.turnOn();
		controller.logMessage("Etat du réfrigérateur : ON");
		
	}
	
	public static void setTemperature(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller, 
						int temperature) throws Exception{
		fridgeOutbound.setTemperature(temperature);
		controller.logMessage("Modification de la température du réfrigérateur:"+temperature);
	}
	
	public static void getMode(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller) throws Exception{
		FridgeMode m = fridgeOutbound.getState();
		controller.logMessage("Etat du réfrigérateur : "+m);
		
	}

}
