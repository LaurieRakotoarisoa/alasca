package components.controller.utilController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import components.controller.EnergyController;
import ports.oven.OvenOutboundPort;
import utils.OvenMode;

public class Oven {
	
	public static LocalDateTime dateToOn;
	public static int temperatureToOn;
	
	
	public static void getMode(OvenOutboundPort ovenOutbound, 
						EnergyController controller) throws Exception{
		OvenMode m = ovenOutbound.getState();
		controller.logMessage("Etat du four : "+m);
		
	}
	
	
	public static void turnOff(OvenOutboundPort ovenOutbound, 
			EnergyController controller) throws Exception{
		ovenOutbound.turnOff();
		controller.logMessage("Etat du four : Off");
		
	}
			
	public static void turnOn(OvenOutboundPort ovenOutbound, 
				EnergyController controller) throws Exception{
		ovenOutbound.turnOn();
		controller.logMessage("Etat du four : ON");
	
	}
	
	public static void turnOnIn(OvenOutboundPort ovenOutbound, 
			EnergyController controller,
			int temperature) throws Exception{
		ovenOutbound.turnOn(temperature);
		dateToOn = null;
		temperature = 0;
		controller.logMessage("Allumer le four en "+temperature+"°");
	
	}
	
	public static void setTemperature(OvenOutboundPort ovenOutbound, 
			EnergyController controller,
			int temperature) throws Exception{
		ovenOutbound.setTemperature(temperature);
		controller.logMessage("Modification de la température à "+temperature+"°");
	
	}
	
	public static void displayDate(OvenOutboundPort ovenOutbound, 
			EnergyController controller, 
			LocalDateTime date, 
			int temperature) throws Exception{
		dateToOn = date;
		temperatureToOn = temperature;
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	    String formattedDate = date.format(myFormatObj);
		controller.logMessage("Allumer le à "+formattedDate+" en "+temperature+"°");
	}

}
