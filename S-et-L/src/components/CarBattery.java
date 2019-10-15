package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.CarBatteryI;
import ports.carBattery.CarBatteryInboundPort;
import utils.BatteryMode;

/**
 * The class <code>CarBattery</code> implements a component that models a car battery's behaviour
 * @author Laurie Rakotoarisoa
 *
 */
@OfferedInterfaces (offered = CarBatteryI.class)

public class CarBattery extends AbstractComponent{
	
	
	/**
	 * Current state of the battery
	 */
	protected BatteryMode state = BatteryMode.InCharge;

	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected CarBattery(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI BatteryInboundPort = new CarBatteryInboundPort(inboundURI,this);
		BatteryInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("car battery") ;
	}
	
	
	/**
	 * Return the current level of the car battery
	 * @return the level battery
	 */
	public int getLevelEnergyService() {
		return 36;
	}
	
	/**
	 * <p>Give information about the current state of the battery</p>
	 * (On, off, In charge)
	 * @return {@link BatteryMode}
	 */
	public BatteryMode getModeService() {
		return state;
	}
	
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting car battery component.") ;
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping car battery component.") ;
		this.printExecutionLogOnFile("battery") ;
		super.finalise();
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(CarBatteryI.class) ;
			p[0].unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
