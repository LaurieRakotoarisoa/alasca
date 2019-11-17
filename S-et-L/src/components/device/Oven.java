package components.device;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.OvenI;
import ports.oven.OvenInboundPort;
import utils.OvenMode;

/**
 * The class <code>Oven</code> implements a component that models a oven's behaviour
 * @author Laurie Rakotoarisoa
 *
 */
@OfferedInterfaces (offered = OvenI.class)

public class Oven extends AbstractComponent{
	
	
	/**
	 * Current state of the oven
	 */
	protected OvenMode state = OvenMode.Off;
	protected int cons = 0;
	/**
	 * the temperature of the oven
	 * between 0 and 250
	 */
	protected int temperature = 0;

	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected Oven(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI ovenInboundPort = new OvenInboundPort(inboundURI,this);
		ovenInboundPort.publishPort();
		//this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("Oven") ;
	}
	
	
	/**
	 * <p>Give information about the current state of the Oven</p>
	 * (On, off)
	 * @return {@link OvenMode}
	 */
	public OvenMode getModeService() {
		return state;
	}
	
	/**
	 * <p>Give information about the current state of the oven</p>
	 * (On, off, In charge)
	 * @return {@link Integer}
	 */
	public int getCon() {
		return cons;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public int setTemperatur(int temperature) {
		if (this.state == OvenMode.On) this.cons = temperature/10;
		this.temperature = temperature;
		this.logMessage("Modification temperature à "+ temperature);
		return temperature;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public OvenMode setModeService(OvenMode state) {
		if (state == OvenMode.Off) this.cons = 0;
		else this.cons = temperature/10;
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
		this.logMessage("starting Oven component.") ;
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping Oven component.") ;
		this.printExecutionLogOnFile("oven") ;
		super.finalise();
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(OvenI.class) ;
			p[0].unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
