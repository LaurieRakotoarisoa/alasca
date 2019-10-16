package components.controller.utilController;

import components.controller.EnergyController;
import ports.carBattery.CarBatteryOutboundPort;
import utils.BatteryMode;

public class CarBattery {
	
	public static void getMode(CarBatteryOutboundPort batteryOutbound, 
						EnergyController controller) throws Exception{
		BatteryMode m = batteryOutbound.getState();
		controller.logMessage("Etat de la batterie : "+m);
		
	}
	
	
	public static void getBattery(CarBatteryOutboundPort batteryOutbound, 
						EnergyController controller) throws Exception{
		int lvl = batteryOutbound.getLevelEnergy();
		controller.logMessage("Niveau de charge de la batterie de voiture : "+lvl);
	}

}
