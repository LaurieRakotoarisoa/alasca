package interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import utils.TurbineMode;

/**
 * The interface <code>WindTurbineI</code> defines the services provided by a wind turbine
 * @author Laurie Rakotoarisoa
 *
 */
public interface WindTurbineI extends OfferedI, RequiredI{
	
	/**
	 * State of the wind turbine according to the environment (suspended or not)
	 * @return the state of the turbine 
	 *
	 * @throws Exception
	 */
	public TurbineMode getState() throws Exception;
	
	
	/**
	 * @return the current production of energy provided by the wind turbine considered as an integer
	 * @throws Exception
	 */
	public int getProduction() throws Exception;
	
	/**
	 * Turn off the wind turbine 
	 * @throws Exception
	 */
	public void turnOff( ) throws Exception;
	
	/**
	 * Turn on the wind turbine 
	 * @throws Exception
	 */
	public void turnOn( ) throws Exception;
	
	

}
