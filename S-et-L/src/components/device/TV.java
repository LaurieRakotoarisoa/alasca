package components.device;

import java.util.HashMap;

import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.tv.mil.models.TVMILCoupledModel;
import clean.equipments.tv.mil.models.TVStateMILModel;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import interfaces.TVI;
import ports.tv.TVInboundPort;
import utils.TVMode;
import utils.fridge.FridgeMode;

/**
 * The class <code>TV</code> implements a component that models a TV's behaviour
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = TVI.class)
public class TV extends AbstractCyPhyComponent
implements EmbeddingComponentAccessI{
	
	public static final String TV_STATE = "tv-state";
	
	/**
	 * Current state of the TV
	 */
	protected TVMode state = TVMode.Off;
	protected int cons = 0;
	protected int backlight = 0;
	protected int lastBacklight = 70;
	protected boolean ecoMode = false;
	
	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected TV(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI TVInboundPort = new TVInboundPort(inboundURI,this);
		TVInboundPort.publishPort();
		//this.executionLog.setDirectory(System.getProperty("user.home"));
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
	 * <p>Give information about the current cons of the TV</p>
	 * (On, off)
	 * @return {@link Integer}
	 */
	public double getCons() {
		return cons;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public int setBacklight(int backlight) {
		if(!ecoMode || backlight <= 30) {
			if(getModeService() == TVMode.On) {
				this.cons = backlight/10;
				this.backlight = backlight;
				this.logMessage("Modification rétroeclairage à "+ backlight);
			}
			else {
				this.logMessage("Télé éteinte pas de modification de rétroéclairage");
			}
		}
		else{
			this.logMessage("Mode économie impossible d'augmenter le rétroéclairage à "+backlight);
		}
		
		return backlight;
	}
	
	/**
	 * <p>set the current state of the TV</p>
	 * (On, off)
	 */
	public TVMode setModeService(TVMode state) {
		assert this.state != state;
		this.state = state;
		if (state == TVMode.Off) {
			this.cons = 0;
			this.lastBacklight = backlight;
			this.backlight = 0;
		}
		else {
			if(ecoMode) {
				this.backlight = 30;
				
			}
			else {
				this.backlight = lastBacklight;
			}
			this.cons = backlight/10;
			
		}
		this.logMessage("Modification state à "+ state+" avec un rétroéclairage de "+this.backlight);
		return state;
	}
	
	public boolean activateEcoMode() {
		assert !ecoMode;
		ecoMode = true;
		if(state == TVMode.On && backlight > 30) {
			lastBacklight = backlight;
			setBacklight(30);
		}
		this.logMessage("Mode eco activé : TV state ->"+state+" backlight at "+backlight);
		return ecoMode;
	}
	
	public boolean deactivateEcoMode() {
		assert ecoMode;
		ecoMode = false;
		if(state == TVMode.On) setBacklight(lastBacklight);
		this.logMessage("Mode economié désactivé : TV state ->"+state+" backlight at "+backlight);
		return ecoMode;
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
		this.logMessage("starting TV component.") ;
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

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return TVMILCoupledModel.buildArchitecture();
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object		getEmbeddingComponentStateValue(String name)
	throws Exception
	{
		if(name.equals(TV_STATE)) return this.state;
		else return name;
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public void		setEmbeddingComponentStateValue(String name,Object value)
	throws Exception
	{
		this.logMessage("access");
		if(name.equals(TV_STATE)) setModeService((TVMode) value);
	}
	
	protected void startSimulation() {
		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((TV)this.getTaskOwner()).
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
			Architecture localArchitecture =
					TVMILCoupledModel.buildArchitecture();
		se = localArchitecture.constructSimulator() ;
		se.setDebugLevel(0) ;
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put(TVStateMILModel.COMPONENT_HOLDER_REF_PARAM_NAME, this);
		se.setSimulationRunParameters(hm);
		System.out.println(se.simulatorAsString()) ;
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
		se.doStandAloneSimulation(0.0, 7000.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
