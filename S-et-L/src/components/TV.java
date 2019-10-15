package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.TVI;
import ports.TVInboudPort;
import utils.TVMode;

/**
 * The class <code>TV</code> implements a component that models a TV's behaviour
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = TVI.class)
public class TV extends AbstractComponent{
	
	/**
	 * Current state of the TV
	 */
	protected TVMode state = TVMode.Off;
	
	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected TV(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI BatteryInboundPort = new TVInboudPort(inboundURI,this);
		BatteryInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home"));
		this.tracer.setTitle("TV");
	}
	
	/**
	 * Return the current level of the TV
	 * @return the level TV
	 */
	public int getLevelEnergyService() {
		return 36;
	}
	
	/**
	 * <p>Give information about the current state of the TV</p>
	 * (On, off, In charge)
	 * @return {@link TVMode}
	 */
	public TVMode getModeService() {
		return state;
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		this.logMessage("starting TV component.") ;
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping TV component.") ;
		this.printExecutionLogOnFile("TV") ;
		super.finalise();
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(TVI.class) ;
			p[0].unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
