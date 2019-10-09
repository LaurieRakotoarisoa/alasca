package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import utils.TurbineMode;

/**
 * The interface <code>WindTurbineI</code> defines the services provided by a wind turbine
 * @author Laurie Rakotoarisoa
 *
 */
public interface WindTurbineI extends OfferedI{
	
	/**
	 * State of the wind turbine according to the environment (wind speed)
	 * @return the state of the turbine 
	 *
	 * @throws Exception
	 */
	public TurbineMode getState() throws Exception;
	
	
	/**
	 * Maximum power that the wind turbine can provided when the wind speed is high
	 * @return the maximum power considerered as an Integer
	 * @throws Exception
	 */
	public int maxPower() throws Exception;
	
	
	/**
	 * @return the current speed of the wind turbine considered as an integer
	 * @throws Exception
	 */
	public int getSpeed() throws Exception;
	
	/**
	 * Operation to stop the wind turbine when needed (security for example)
	 * @throws Exception
	 */
	public void stop() throws Exception;
	

}
