package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.FridgeMode;

/**
 * The interface <code>TVI</code> defines the services 
 * to access car TV state and its operations
 * 
 * @author Saad CHIADMI
 */
public interface FridgeI extends OfferedI, RequiredI{

	
	/**
	 * Give information about the current state of the TV
	 * @return  a state from {@link TVMode} enum
	 * @throws Exception
	 */
	public FridgeMode getState() throws Exception;
	
//	/**
//	 * Turn off the Fridge
//	 * @throws Exception
//	 */
//	public void turnOff( ) throws Exception;
//	
//	/**
//	 * Turn on the TV 
//	 * @throws Exception
//	 */
//	public void turnOn( ) throws Exception;
//	
//	/**
//	 * Turn on the TV 
//	 * @throws Exception
//	 */
//	public void pause( ) throws Exception;
	
}
