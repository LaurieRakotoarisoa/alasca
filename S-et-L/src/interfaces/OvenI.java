
package interfaces;


import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.oven.OvenLightMode;
import utils.oven.OvenMode;

/**
 * The interface <code>OvenI</code> defines the services 
 * to access Oven state and its operations
 * 
 * @author Laurie Rakotoarisoa
 */
public interface OvenI extends OfferedI, RequiredI{
	
	
	/**
	 * Give information about the current state of the oven
	 * @return  a state from {@link OvenMode} enum
	 * @throws Exception
	 */
	public OvenMode getState() throws Exception;
	
	/**
	 * Give information about the current consommation of the oven
	 * @return  a state from {@link Integer}
	 * @throws Exception
	 */
	public int getCons() throws Exception;
	
	/**
	 * Turn off the oven
	 * @throws Exception
	 */
	public void turnOff() throws Exception;
	
	/**(
	 * Turn on the oven 
	 * @throws Exception
	 */
	public void turnOn() throws Exception;
	
	/**
	 * Update the temperature of the oven 
	 * @throws Exception
	 */
	public void setTemperature(int temperature) throws Exception;
	
	/**
	 * Turn on the oven on specific temperature
	 * @throws Exception
	 */
	public void turnOn(int temperature) throws Exception;
	
	/**
	 * Set mode of Oven while cooking
	 * @param mode from {@link OvenLightMode} enum
	 * @throws Exception
	 */
	public void setModeLight(OvenLightMode mode) throws Exception;
	
	
	/**
	 * Forbid cleaning oven with pyrolysis mode
	 * @throws Exception
	 */
	public void forbidPyrolysis() throws Exception;
	
	
	/**
	 * Allow cleaning oven with pyrolysis mode
	 * @throws Exception
	 */
	public void allowPyrolysis() throws Exception;

}
