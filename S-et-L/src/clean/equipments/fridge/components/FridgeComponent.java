package clean.equipments.fridge.components;

import clean.equipments.fridge.mil.FridgeConsumptionMILModel;
import clean.equipments.fridge.mil.FridgeMILCoupledModel;
import clean.equipments.fridge.mil.FridgeStateMILModel;
import clean.equipments.fridge.sil.FridgeSILCoupledModel;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.EquipmentDirectory;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryerSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.components.HairDryer.HairDryerMode;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.ports.HairDryerInboundPort;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.sil.models.HairDryerSILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.interfaces.EnergyManagerCI;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
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

	protected FridgeMode state;
	protected double consumption;
	protected double temperature;
	protected boolean ecoActivated;
	
	protected FridgeInboundPort servicesInboundPort;
	protected final String servicesInboundPortURI = "fridge inbound";
	
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
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
			} else if (simArchitectureURI.equals(HairDryer.SIL_STAND_ALONE)) {
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

	
}
