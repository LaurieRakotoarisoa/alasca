package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.TVI;
import ports.tv.TVInboudPort;
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
	protected int cons = 0;
	protected int backlight = 50;
	
	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected TV(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI TVInboundPort = new TVInboudPort(inboundURI,this);
		TVInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home"));
		this.tracer.setTitle("TV");
	}
	
	/**
	 * <p>Give information about the current state of the TV</p>
	 * (On, off)
	 * @return {@link TVMode}
	 */
	public TVMode getModeService() {
		return state;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public int setBacklight(int backlight) {
		this.cons = backlight/10;
		this.backlight = backlight;
		this.logMessage("Modification rétroeclairage à "+ backlight);
		return backlight;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public TVMode setModeService(TVMode state) {
		if (state == TVMode.Off) this.cons = 0;
		else this.cons = backlight/10;
		this.state = state;
		this.logMessage("Modification state à "+ state);
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
