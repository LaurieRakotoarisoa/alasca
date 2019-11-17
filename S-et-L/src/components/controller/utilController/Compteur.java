package components.controller.utilController;

import components.controller.EnergyController;
import ports.compteur.CompteurOutboundPort;

public class Compteur {
	public static int getComsumption(CompteurOutboundPort p, EnergyController controller) throws Exception {
		int cons =  p.getConsumptionOfAllDevices();
		return cons;
	}
}
