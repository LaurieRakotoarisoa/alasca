package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>CompteurI</code> defines the services 
 * to access Compteur state and its operations
 * 
 * @author Saad CHIADMI
 */
public interface CompteurI extends OfferedI, RequiredI{
	
	/**
	 * Give information about the current state of the consumption
	 * @return  addition of all consumption
	 * @throws Exception
	 */
	public int getConsumptionOfAllDevices() throws Exception;
	
	public void setConsumptionOfAllDevices(int cons) throws Exception;

}
