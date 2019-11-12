package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>ProductionI</code> defines the services 
 * to access Production state and its operations
 * 
 * @author Saad CHIADMI
 */
public interface ProductionI extends OfferedI, RequiredI{
	
	/**
	 * @return the current production of energy provided by the Production considered as an integer
	 * @throws Exception
	 */
	public int getProduction() throws Exception;
	
	/**
	 * Update the current production of energy provided by the Production considered as an integer
	 * @throws Exception
	 */
	public void setProduction(int production) throws Exception;

}
