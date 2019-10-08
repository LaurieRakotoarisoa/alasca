
package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.BatteryMode;

/**
 * The interface <code>CarBatteryI</code> defines the services 
 * to access car battery state and its operations
 * 
 * @author Laurie Rakotoarisoa
 */
public interface CarBatteryI extends OfferedI, RequiredI{
	
	
	/**
	 * @return the level of the car battery considered as an integer
	 * @throws Exception
	 */
	public int getLevelEnergy() throws Exception;
	
	
	/**
	 * Give information about the current state of the battery
	 * @return  a state from {@link BatteryMode} enum
	 * @throws Exception
	 */
	public BatteryMode getState() throws Exception;

}
