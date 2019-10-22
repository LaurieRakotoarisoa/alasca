package components.device;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.FridgeI;
import ports.fridge.FridgeInboundPort;
import utils.FridgeMode;

/**
 * The class <code>Fridge</code> implements a component that models a Fridge's behaviour
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = FridgeI.class)
public class Fridge extends AbstractComponent{
	
	/**
	 * Current state of the Fridge
	 */
	protected FridgeMode state = FridgeMode.On_Close;
	/**
	 * Current consommation
	 */
	protected int cons = 0;
	/**
	 * the temperature of the fridge
	 * between 0 and 5
	 */
	protected int temperature = 3;
	
	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected Fridge(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI FridgeInboundPort = new FridgeInboundPort(inboundURI,this);
		FridgeInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home"));
		this.tracer.setTitle("Fridge");
	}
	
	/**
	 * <p>Give information about the current state of the Fridge</p>
	 * (On, off, In charge)
	 * @return {@link FridgeMode}
	 */
	public FridgeMode getModeService() {
		return state;
	}
	
	/**
	 * <p>Give information about the current cons of the Fridge</p>
	 * (On, off, In charge)
	 * @return {@link Integer}
	 */
	public int getCons() {
		return cons;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public int setTemperature(int temperature) {
		if (this.state == FridgeMode.On_Open) this.cons = temperature*4;
		else if(this.state == FridgeMode.On_Close) this.cons = temperature*2;
		this.temperature = temperature;
		this.logMessage("Modification temperature à "+ temperature);
		return temperature;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public FridgeMode setModeService(FridgeMode state) {
		if (state == FridgeMode.Off_Close) this.cons = 0;
		else if (state == FridgeMode.Off_Open) this.cons = 0;
		else if (state == FridgeMode.On_Open)this.cons = temperature*4;
		else this.cons = temperature*2;
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
		this.logMessage("starting Fridge component.") ;
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping Fridge component.") ;
		this.printExecutionLogOnFile("Fridge") ;
		super.finalise();
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			PortI[] p = this.findPortsFromInterface(FridgeI.class) ;
			p[0].unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}

