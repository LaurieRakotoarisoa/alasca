package simulation.Fridge;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.Fridge.events.HighTemperatureEvent;
import simulation.Fridge.events.LowTemperatureEvent;
import utils.fridge.FridgeMode;

@ModelExternalEvents (imported = {LowTemperatureEvent.class,
								  HighTemperatureEvent.class})
public class FridgeState 
extends AtomicModel{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public FridgeState(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "fridge-compressors";
	
	private State_Compressors currentCompressorsState;
	
	private FridgeMode currentFridgeState;
	
	/** enumeration describing which compressors are active */
	protected enum State_Compressors{
		BOTH_ACTIVE, ONE_ACTIVE, NONE
	}

	
	// -------------------------------------------------------------------------
	// Simulation methods and protocol
	// -------------------------------------------------------------------------

	@Override
	public Vector<EventI> output() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return Duration.INFINITY;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentCompressorsState = State_Compressors.ONE_ACTIVE;
		this.currentFridgeState = FridgeMode.On_Close;
		super.initialiseState(initialTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;
		Vector<EventI> current = this.getStoredEventAndReset();
		assert current != null & current.size() == 1;
		EventI e = current.get(0);
		
		if(e instanceof LowTemperatureEvent) {
			assert (this.currentFridgeState == FridgeMode.Off_Close
					|| this.currentFridgeState == FridgeMode.Off_Open);
			this.lowTempProcess();
		}
	}
	
	/**
	 * active compressors accordingly to the state of the doors 
	 */
	private void lowTempProcess() {
		if(this.currentFridgeState == FridgeMode.Off_Open) {
			this.currentCompressorsState = State_Compressors.BOTH_ACTIVE;
			this.currentFridgeState = FridgeMode.On_Open;
		}
		else if(this.currentFridgeState == FridgeMode.Off_Close) {
			this.currentCompressorsState = State_Compressors.ONE_ACTIVE;
			this.currentFridgeState = FridgeMode.On_Close;
		}
		
	}

}
