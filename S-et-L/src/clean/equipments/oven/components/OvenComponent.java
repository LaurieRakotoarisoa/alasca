package clean.equipments.oven.components;

import java.util.HashMap;

import clean.equipments.oven.mil.OvenConsumptionMILModel;
import clean.equipments.oven.mil.OvenMILCoupledModel;
import clean.equipments.oven.sil.OvenSILCoupledModel;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.OvenI;
import ports.oven.OvenInboundPort;
import utils.oven.OvenLightMode;
import utils.oven.OvenMode;

@OfferedInterfaces(offered = OvenI.class)
public class OvenComponent 
extends AbstractCyPhyComponent
implements OvenI, EmbeddingComponentAccessI{
	
	protected OvenComponent(String simArchitectureURI) {
		super(3, 0);
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	public static final String TARG_TEMP = "target temperature";
	public static final String ECO_MODE = "economy";
	public static final String Oven_STATE = "oven state";

	protected OvenMode state;
	OvenLightMode mode;
	protected int consumption;
	protected int temperature;
	protected boolean ecoActivated;
	
	protected OvenInboundPort servicesInboundPort;
	protected final String servicesInboundPortURI = "oven inbound";
	
	public static final String	SIL_STAND_ALONE =
			"oven SIL stand alone architecture";
	
	/** the simulation mode used for the current execution.					*/
	protected SimulationArchitectures.SimulationMode	simMode ;
	
	

	@Override
	public OvenMode getState() throws Exception {
		return state;
	}

	@Override
	public int getCons() throws Exception {
		return consumption;
	}

	@Override
	public void turnOff() throws Exception {
		assert state == OvenMode.On;
		if(state == OvenMode.On) state = OvenMode.Off;
	}

	@Override
	public void turnOn() throws Exception {
		assert state == OvenMode.Off;
		if(state == OvenMode.Off) state = OvenMode.On;
	}
	
	@Override
	public void turnOn(int temperature) throws Exception {
		assert state == OvenMode.Off;
		if(state == OvenMode.Off) state = OvenMode.On;
		this.temperature = temperature;
	}

	@Override
	public void setModeLight(OvenLightMode mode) throws Exception {
		this.mode = mode;
	}

	@Override
	public void forbidPyrolysis() throws Exception {}

	@Override
	public void allowPyrolysis() throws Exception {	}

	@Override
	public void setTemperature(int temperature) throws Exception {
		this.temperature = temperature;
		
	}
	
	@Override
	public void activateEcoMode() throws Exception {
		assert !ecoActivated;
		this.ecoActivated = true;
		
	}

	@Override
	public void deactivateEcoMode() throws Exception {
		assert ecoActivated;
		this.ecoActivated = false;
		
	}

	@Override
	protected Architecture createLocalArchitecture(String modelURI) throws Exception {
		assert	modelURI != null ;

		if (modelURI.equals(OvenMILCoupledModel.URI)) {
			return OvenMILCoupledModel.buildArchitecture() ;
		} else if (modelURI.equals(OvenSILCoupledModel.URI)) {
			return OvenSILCoupledModel.buildArchitecture() ;
		} else {
			throw new Exception("unknown model URI: " + modelURI + ".") ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object		getEmbeddingComponentStateValue(String name)
	throws Exception
	{
		if(name == TARG_TEMP) return temperature;
		else if(name == ECO_MODE) return ecoActivated;
		else {assert name == Oven_STATE; return state;}
	}
	
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	
	/**
	 * initialise the hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simArchitectureURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simArchitectureURI		the URI of the simulation architecture to be created and run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(String simArchitectureURI) throws Exception
	{
		if (!simArchitectureURI.equals(SimulationArchitectures.NONE)) {
			String modelURI = null ;
			if (simArchitectureURI.equals(SimulationArchitectures.MIL)) {
				modelURI = OvenMILCoupledModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.MIL ;
			} else if (simArchitectureURI.equals(OvenComponent.SIL_STAND_ALONE)) {
				modelURI = OvenSILCoupledModel.URI ;
				this.simMode =
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE ;
			} else if (simArchitectureURI.equals(SimulationArchitectures.SIL)) {
				modelURI = OvenSILCoupledModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.SIL ;
			} else {
				throw new Exception("unknown simulation architecture URI: " +
														simArchitectureURI) ;
			}
			// The coupled model has been made able to create the simulation
			// architecture description.
			Architecture localArchitecture =
										this.createLocalArchitecture(modelURI) ;
			// Create the appropriate DEVS simulation plug-in.
			this.asp = new OvenSimulatorPlugin() ;
			// Set the URI of the plug-in, using the URI of its associated
			// simulation model.
			this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
			// Set the simulation architecture.
			this.asp.setSimulationArchitecture(localArchitecture) ;
			// Install the plug-in on the component, starting its own life-cycle.
			this.installPlugin(this.asp) ;
		} else {
			this.simMode = null ;
		}

		this.servicesInboundPort = new OvenInboundPort(servicesInboundPortURI,this) ;
		this.servicesInboundPort.publishPort() ;

		// Toggle logging on to get a log on the screen.
		this.tracer.setTitle("OVEN") ;
		this.tracer.setRelativePosition(1, 1) ;
		this.toggleTracing() ;	
	}

	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		
		this.state = OvenMode.On;
		this.consumption = (int) OvenConsumptionMILModel.DEFAULT_CONS;
		this.temperature = 4;
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (this.simMode == SimulationArchitectures.SimulationMode.SIL ||
				   										this.simMode == null) {
		} else if (this.simMode ==
					SimulationArchitectures.SimulationMode.SIL_STAND_ALONE) {
			this.silStandAloneSimulation() ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		
		if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
			this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
		{
			//nothing
		}

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.servicesInboundPort.unpublishPort() ;
			if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
				this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
			{
				//nothing
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.servicesInboundPort.unpublishPort() ;
			if (this.simMode != SimulationArchitectures.SimulationMode.MIL &&
				this.simMode !=
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE)
			{
				//nothing for now;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}
	
	protected void		silStandAloneSimulation() throws Exception
	{
		// Start the simulation.
		this.runTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((OvenComponent)this.getTaskOwner()).
												silStandAloneSimulationRun() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}) ;
	}
	
	/**
	 * running the SIL model for the Oven for 500 seconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		silStandAloneSimulationRun() throws Exception
	{
		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		this.asp.setSimulationRunParameters(simParams) ;
		asp.doStandAloneSimulation(0.0, 500.0) ;
		Thread.sleep(5000) ;
		asp.finaliseSimulation() ;
	}

	

	

	
}
