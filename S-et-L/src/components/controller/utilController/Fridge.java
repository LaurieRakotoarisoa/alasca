package components.controller.utilController;

import components.controller.EnergyController;
import ports.fridge.FridgeOutboundPort;
import utils.FridgeMode;

public class Fridge {
	
	public static void TurnOff(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller) throws Exception{
		fridgeOutbound.turnOff();
		controller.logMessage("Etat du réfrigérateur : Off");
		
	}
	
	public static void TurnOn(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller) throws Exception{
		fridgeOutbound.turnOn();
		controller.logMessage("Etat du réfrigérateur : ON");
		
	}
	
	public static void SetTemperature(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller, 
						int temperature) throws Exception{
		fridgeOutbound.setTemperatur(temperature);
		controller.logMessage("Modification de la température du réfrigérateur:"+temperature);
	}
	
	public static void getMode(FridgeOutboundPort fridgeOutbound, 
						EnergyController controller) throws Exception{
		FridgeMode m = fridgeOutbound.getState();
		controller.logMessage("Etat du réfrigérateur : "+m);
		
	}

}
