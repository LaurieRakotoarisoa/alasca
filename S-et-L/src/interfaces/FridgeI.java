package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.TVMode;
import utils.fridge.FridgeMode;

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
	public double getCons() throws Exception;
	
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
	 * set the target temperature of the Fridge
	 * @throws Exception
	 */
	public void setTemperature(double temperature) throws Exception;
	
	/**
	 * activate economy mode for the fridge
	 * @throws Exception
	 */
	public void activateEcoMode() throws Exception;
	
	/**
	 * deactivate economy mode for the fridge
	 * @throws Exception
	 */
	public void deactivateEcoMode() throws Exception;
	
	/**
	 * open the door of the fridge
	 * @throws Exception
	 */
	public void openDoor() throws Exception;
	
	/**
	 * close the door of the fridge
	 * @throws Exception
	 */
	public void closeDoor() throws Exception;
	
	
	
}
