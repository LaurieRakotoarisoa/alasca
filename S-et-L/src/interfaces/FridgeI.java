package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.FridgeMode;

/**
 * The interface <code>FridgeI</code> defines the services 
 * to access Fridge state and its operations
 * 
 * @author Saad CHIADMI
 */
public interface FridgeI extends OfferedI, RequiredI{

	
	/**
	 * Give information about the current state of the Fridge
	 * @return  a state from {@link FridgeMode} enum
	 * @throws Exception
	 */
	public FridgeMode getState() throws Exception;
	
	/**
	 * Give information about the current state of the TV
	 * @return  a state from {@link TVMode} enum
	 * @throws Exception
	 */
	public int getCons() throws Exception;
	
	/**
	 * Turn off the Fridge
	 * @throws Exception
	 */
	public void turnOff( ) throws Exception;
	
	/**
	 * Turn on the Fridge
	 * @throws Exception
	 */
	public void turnOn( ) throws Exception;
	
	/**
	 * set the temperature of the Fridge
	 * @throws Exception
	 */
	public void setTemperature(int temperature) throws Exception; 
	
}
