
package interfaces;


import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.OvenMode;

/**
 * The interface <code>CarBatteryI</code> defines the services 
 * to access car battery state and its operations
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
	 * Give information about the current cons of the oven
	 * @return  a state from {@link Integer} enum
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
	 * Turn on the oven on specific date
	 * @throws Exception
	 */
	public void turnOn(int temperature) throws Exception;

}
