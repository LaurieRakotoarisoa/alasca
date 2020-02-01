package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.TVMode;

/**
 * The interface <code>TVI</code> defines the services 
 * to access TV state and its operations
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
	 * Give information about the current state of the TV
	 * @return  a state from {@link TVMode} enum
	 * @throws Exception
	 */
	public double getCons() throws Exception;
	
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
	
	/**
	 * put the tv in economy mode i.e change backlight if it's on
	 * @throws Exception
	 */
	public void activateEcoMode() throws Exception;
	
	/**
	 * deactivate tv economy mode i.e change backlight if it's on
	 * @throws Exception
	 */
	public void deactivateEcoMode() throws Exception;
	
	
	
}
