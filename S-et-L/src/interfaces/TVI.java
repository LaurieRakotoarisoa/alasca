package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.TVMode;

/**
 * The interface <code>TVI</code> defines the services 
 * to access car TV state and its operations
 * 
 * @author Saad CHIADMI
 */
public interface TVI extends OfferedI, RequiredI{

	
	/**
	 * Give information about the current state of the TV
	 * @return  a state from {@link TVMode} enum
	 * @throws Exception
	 */
	public TVMode getState() throws Exception;
	
	/**
	 * Turn off the TV 
	 * @throws Exception
	 */
	public void turnOff( ) throws Exception;
	
	/**
	 * Turn on the TV 
	 * @throws Exception
	 */
	public void turnOn( ) throws Exception;
	
	/**
	 * set the backlight of the TV 
	 * @throws Exception
	 */
	public void setBacklight(int backlight) throws Exception;
	
	
	
}