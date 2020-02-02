package components.device;

import java.util.HashMap;

import clean.equipments.fridge.components.FridgeSimulatorPlugin;
import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.fridge.sil.FridgeSILCoupledModel;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import interfaces.FridgeI;
import ports.fridge.FridgeInboundPort;
import utils.fridge.FridgeMode;

/**
 * The class <code>Fridge</code> implements a component that models a Fridge's behaviour
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = FridgeI.class)
public class Fridge extends AbstractCyPhyComponent
implements EmbeddingComponentAccessI{
	
	/**
	 * Current state of the Fridge
	 */
	protected FridgeMode state = FridgeMode.On_Close;
	/**
	 * Current consommation
	 */
	protected double cons = 0;
	/**
	 * the temperature of the fridge
	 * between 0 and 5
	 */
	protected double temperature = 3;
	
	/**
	 * true if economy mode is activated
	 */
	protected boolean ecoMode = false;
	
	public static final String TARG_TEMP = "target temperature";
	public static final String ECO_MODE = "economy";
	public static final String FRIDGE_STATE = "fridge state";
	public static final String FRIDGE_CONS = "fridge consumption";
	
	/** the simulation plug-in holding the simulation models.				*/
	protected FridgeSimulatorPlugin					asp ;
	
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
		//this.executionLog.setDirectory(System.getProperty("user.home"));
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
	 * @return {@link Integer}
	 */
	public double getCons() {
		return cons;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public double setTemperature(double temperature) {
		if (this.state == FridgeMode.On_Open) this.cons = temperature*4;
		else if(this.state == FridgeMode.On_Close) this.cons = temperature*2;
		this.temperature = temperature;
		this.logMessage("Modification temperature à "+ temperature);
		return temperature;
	}
	
	/**
	 * <p>set the current state of the Fridge</p>
	 * (On, off)
	 */
	public FridgeMode setModeService(FridgeMode state) {
		this.state = state;
		this.logMessage("Modification state à "+ state);
		return state;
	}
	
	public boolean setEcoMode(boolean mode) {
		assert ecoMode != mode;
		this.ecoMode = mode;
		return mode;
	}
	
	public FridgeMode openDoor() {
		
		if(state == FridgeMode.On_Close) state = FridgeMode.On_Open;
		else {
			assert state == FridgeMode.Off_Close;
			state = FridgeMode.Off_Open;
		}
		return state;
	}
	
	public FridgeMode closeDoor() {
		
		if(state == FridgeMode.On_Open) state = FridgeMode.On_Close;
		else {
			assert state == FridgeMode.Off_Open;
			state = FridgeMode.Off_Close;
		}
		return state;
	}
	
	public double updateCons(double cons) {
		this.cons = cons;
		this.logMessage("consumption updated "+this.cons);
		return cons;
	}
	
	/**
	 * initialise the fridge component.
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
		this.logMessage("starting Fridge component.") ;
	}
	
	@Override
	public void			execute() throws Exception
	{
		this.logMessage("execute");
		silStandAloneSimulationRun();
		
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

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return FridgeSILCoupledModel.buildArchitecture();
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
		else if(name == FRIDGE_CONS) return cons;
		else {assert name == FRIDGE_STATE; return state;}
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public void		setEmbeddingComponentStateValue(String name,Object value)
	throws Exception
	{
		if(name == TARG_TEMP) temperature = (Double) value;
		else if(name == ECO_MODE) ecoMode = (Boolean) value;
		else if(name == FRIDGE_CONS) updateCons((Double) value);
		else {
			setModeService(((FridgeMode) value));
		}
	}
	
	protected void startSimulation() {
		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((Fridge)this.getTaskOwner()).
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
			Architecture localArchitecture = FridgeMILCoupledModel.buildArchitecture() ;
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put(FridgeStateMILModel.COMPONENT_HOLDER_REF_PARAM_NAME, this);
			hm.put(FridgeConsumptionMILModel.COMPONENT_HOLDER_REF_PARAM_NAME, this);
			se.setSimulationRunParameters(hm);
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
			se.doStandAloneSimulation(0.0, 5000.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}

