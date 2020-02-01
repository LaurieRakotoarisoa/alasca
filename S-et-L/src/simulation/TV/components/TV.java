package simulation.TV.components;

import java.util.HashMap;

import clean.equipments.tv.mil.models.TVMILCoupledModel;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.examples.hem.simulations.SimulationArchitectures;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import interfaces.TVI;
import simulation.TV.models.TVStateModel;
import utils.TVMode;
@OfferedInterfaces (offered = TVI.class)
public class TV 
extends		AbstractCyPhyComponent
implements	TVI,EmbeddingComponentStateAccessI{
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	protected TVSimulatorPlugin asp;
	
	/** the simulation mode used for the current execution.					*/
	protected SimulationArchitectures.SimulationMode simMode;
	
	/** the URI of the simulation architecture used when performing a stand
	 *  alone SIL simlation, typically to perform unit testing.				*/
	public static final String	SIL_STAND_ALONE =
									"TV SIL stand alone architecture" ;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected TV(String simArchitectureURI) throws Exception {
		super(2,0);
		assert simArchitectureURI != null;
		this.initialise(simArchitectureURI);
	}
	
	protected TV(String reflectionInboundPortURI,String simArchitectureURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		assert simArchitectureURI != null;
		this.initialise(simArchitectureURI) ;
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture	createLocalArchitecture(String architectureURI)
	throws Exception
	{
		return TVMILCoupledModel.buildArchitecture();
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
		// To give an example of the embedding component access facility, the
		// following lines show how to set the reference to the embedding
		// component or a proxy responding to the access calls.
		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put("componentRef", this) ;
		this.asp.setSimulationRunParameters(simParams) ;
		System.out.println("hello");
		this.logMessage("start execution");
		// Start the simulation.
		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							asp.doStandAloneSimulation(0.0, 7000.0) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}) ;
		Thread.sleep(10L) ;
		// During the simulation, the following lines provide an example how
		// to use the simulation model access facility by the component.
		for (int i = 0 ; i < 100 ; i++) {
			this.logMessage("TV " +
				this.asp.getModelStateValue(TVStateModel.URI, "state"));
			Thread.sleep(5L) ;
		}
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		return TVStateModel.DEFAULT_TV_BACKLIGHT;
	}
	
	protected void initialise(String simArchitectureURI) throws Exception{
		
		//NOT DONE
	}

	@Override
	public TVMode getState() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCons() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void turnOff() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnOn() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBacklight(int backlight) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateEcoMode() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivateEcoMode() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
