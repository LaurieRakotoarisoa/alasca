package components.device;

import java.util.HashMap;

import clean.equipments.oven.components.OvenSimulatorPlugin;
import clean.equipments.oven.mil.OvenConsumptionMILModel;
import clean.equipments.oven.mil.OvenMILCoupledModel;
import clean.equipments.oven.mil.OvenStateMILModel;
import clean.equipments.oven.sil.OvenSILCoupledModel;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import interfaces.OvenI;
import ports.oven.OvenInboundPort;
import utils.oven.OvenLightMode;
import utils.oven.OvenMode;

/**
 * The class <code>Oven</code> implements a component that models a oven's behaviour
 * @author Laurie Rakotoarisoa
 *
 */
@OfferedInterfaces (offered = OvenI.class)

public class Oven extends AbstractCyPhyComponent
implements EmbeddingComponentAccessI{
	
	
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
	 * true if economy mode is activated
	 */
	protected boolean ecoMode = false;

	public static final String TARG_TEMP = "target temperature";
	public static final String ECO_MODE = "economy";
	public static final String Oven_STATE = "oven state";
	public static final String Oven_CONS = "oven consumption";
	
	/** the simulation plug-in holding the simulation models.				*/
	protected OvenSimulatorPlugin					asp ;
	
	
	/**
	 * default light mode of the oven 
	 */
	protected OvenLightMode lightMode = OvenLightMode.ON;
	
	/**
	 * initially allow pyrolysis
	 */
	protected boolean allow_pyrolysis = true;

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
	
	public double updateCons(int cons) {
		this.cons = cons;
		this.logMessage("consumption updated "+this.cons);
		return cons;
	}
	
	/**
	 * <p>set the current state of the oven</p>
	 * (On, off)
	 */
	public int setTemperatur(int temperature) {
		if (this.state == OvenMode.On) this.cons = temperature/10;
		this.temperature = temperature;
		this.logMessage("Modification temperature à "+ temperature);
		return temperature;
	}
	
	public boolean setEcoMode(boolean mode) {
		assert ecoMode != mode;
		this.ecoMode = mode;
		return mode;
	}
	
	/**
	 * <p>set the current state of the oven</p>
	 * (On, off)
	 */
	public OvenMode setModeService(OvenMode state) {
		if (state == OvenMode.Off) this.cons = 0;
		else this.cons = temperature/10;
		this.state = state;
		this.logMessage("Modification state à "+ state);
		return state;
	}
	
	public OvenLightMode setModeLight(OvenLightMode mode) {
		this.lightMode = mode;
		this.logMessage("Light mode is now : "+lightMode);
		return mode;
	}
	
	/**
	 * initialise the oven component.
	 *
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(String simArchitectureURI) throws Exception
	{
		
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
	public void			execute() throws Exception
	{
		this.logMessage("execute");
		startSimulation();
		
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping Oven component.") ;
		this.printExecutionLogOnFile("Oven") ;
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
	
	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return OvenSILCoupledModel.buildArchitecture();
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object		getEmbeddingComponentStateValue(String name)
	throws Exception
	{
		if(name == TARG_TEMP) return temperature;
		else if(name == ECO_MODE) return ecoMode;
		else if(name == Oven_CONS) return cons;
		else {assert name == Oven_STATE; return state;}
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public void		setEmbeddingComponentStateValue(String name,Object value)
	throws Exception
	{
		if(name == TARG_TEMP) temperature = (int) value;
		else if(name == ECO_MODE) ecoMode = (Boolean) value;
		else if(name == Oven_CONS) updateCons((int)value);
		else {
			setModeService(((OvenMode) value));
		}
	}
	
	protected void startSimulation() {
		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Oven)this.getTaskOwner()).
													silStandAloneSimulationRun() ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}) ;
	}
	
	protected void		silStandAloneSimulationRun() throws Exception
	{
		SimulationEngine se;
		try {
			Architecture localArchitecture = OvenMILCoupledModel.buildArchitecture() ;
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put(OvenStateMILModel.COMPONENT_HOLDER_REF_PARAM_NAME, this);
			hm.put(OvenConsumptionMILModel.COMPONENT_HOLDER_REF_PARAM_NAME, this);
			se.setSimulationRunParameters(hm);
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
			se.doStandAloneSimulation(0.0, 5000.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
