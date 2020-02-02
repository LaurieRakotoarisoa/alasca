package clean.equipments.fridge.components;

import java.util.HashMap;

import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.fridge.sil.FridgeSILCoupledModel;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.interfaces.HairDryerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import interfaces.FridgeI;
import ports.fridge.FridgeInboundPort;
import utils.fridge.FridgeMode;

@OfferedInterfaces(offered = FridgeI.class)
public class FridgeComponent 
extends AbstractCyPhyComponent
implements FridgeI, EmbeddingComponentAccessI{
	
	protected FridgeComponent(String simArchitectureURI) {
		super(3, 0);
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	public static final String TARG_TEMP = "target temperature";
	public static final String ECO_MODE = "economy";
	public static final String FRIDGE_STATE = "fridge state";

	protected FridgeMode state;
	protected double consumption;
	protected double temperature;
	protected boolean ecoActivated;
	
	protected FridgeInboundPort servicesInboundPort;
	protected final String servicesInboundPortURI = "fridge inbound";
	
	public static final String	SIL_STAND_ALONE =
			"fridge SIL stand alone architecture";
	
	/** the simulation mode used for the current execution.					*/
	protected SimulationArchitectures.SimulationMode	simMode ;
	
	

	@Override
	public FridgeMode getState() throws Exception {
		return state;
	}

	@Override
	public double getCons() throws Exception {
		return consumption;
	}

	@Override
	public void turnOff() throws Exception {
		assert state == FridgeMode.On_Close || state == FridgeMode.On_Open;
		if(state == FridgeMode.On_Open) state = FridgeMode.Off_Open;
		else state = FridgeMode.Off_Close;
		
	}

	@Override
	public void turnOn() throws Exception {
		assert state == FridgeMode.Off_Close || state == FridgeMode.Off_Open;
		if(state == FridgeMode.Off_Open) state = FridgeMode.On_Open;
		else state = FridgeMode.On_Close;
		
	}

	@Override
	public void setTemperature(double temperature) throws Exception {
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
	public void openDoor() throws Exception {
		if(state == FridgeMode.On_Close) state = FridgeMode.On_Open;
		else {
			assert state == FridgeMode.Off_Close;
			state = FridgeMode.Off_Open;
		}
		
	}

	@Override
	public void closeDoor() throws Exception {
		if(state == FridgeMode.On_Open) state = FridgeMode.On_Close;
		else {
			assert state == FridgeMode.Off_Open;
			state = FridgeMode.Off_Close;
		}
		
	}

	@Override
	protected Architecture createLocalArchitecture(String modelURI) throws Exception {
		assert	modelURI != null ;

		if (modelURI.equals(FridgeMILCoupledModel.URI)) {
			return FridgeMILCoupledModel.buildArchitecture() ;
		} else if (modelURI.equals(FridgeSILCoupledModel.URI)) {
			return FridgeSILCoupledModel.buildArchitecture() ;
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
		else {assert name == FRIDGE_STATE; return state;}
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
				modelURI = FridgeMILCoupledModel.URI ;
				this.simMode = SimulationArchitectures.SimulationMode.MIL ;
			} else if (simArchitectureURI.equals(FridgeComponent.SIL_STAND_ALONE)) {
				modelURI = FridgeSILCoupledModel.URI ;
				this.simMode =
						SimulationArchitectures.SimulationMode.SIL_STAND_ALONE ;
			} else if (simArchitectureURI.equals(SimulationArchitectures.SIL)) {
				modelURI = FridgeSILCoupledModel.URI ;
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
			this.asp = new FridgeSimulatorPlugin() ;
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

		this.servicesInboundPort = new FridgeInboundPort(servicesInboundPortURI,this) ;
		this.servicesInboundPort.publishPort() ;

		// Toggle logging on to get a log on the screen.
		this.tracer.setTitle("Fridge") ;
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
		
		this.state = FridgeMode.On_Close;
		this.consumption = FridgeConsumptionMILModel.DEFAULT_CONS;
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
						((FridgeComponent)this.getTaskOwner()).
												silStandAloneSimulationRun() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}) ;
	}
	
	/**
	 * running the SIL model for the fridge for 500 seconds.
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
