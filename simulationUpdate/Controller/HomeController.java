package simulation.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.environment.electricity.events.RestoreElecEvent;
import simulation.environment.electricity.events.SheddingEvent;

@ModelExternalEvents (imported = {SheddingEvent.class, RestoreElecEvent.class },
						exported = {NoEconomyEvent.class, EconomyEvent.class})
public class HomeController 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public HomeController(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.reduceCons = false;
		this.restoreCons = false;
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** the URI to be used when creating the instance of the model.			*/
	public static final String	URI = "Home-Controller" ;
	
	private boolean reduceCons;
	private boolean restoreCons;
	
	// -------------------------------------------------------------------------
	// Simulation methods and protocol
	// -------------------------------------------------------------------------
	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = new Vector<EventI>();
		if(restoreCons) {
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			NoEconomyEvent e = new NoEconomyEvent(t);
			ret.add(e);
			restoreCons = false;
			return ret;
		}
		else if(reduceCons) {
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			EconomyEvent e = new EconomyEvent(t);
			ret.add(e);
			reduceCons = false;
			return ret;
		}
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if(this.reduceCons || this.restoreCons) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		}
		return Duration.INFINITY;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		Vector<EventI> current = this.getStoredEventAndReset() ;
		assert current != null & current.size() == 1;
		EventI e = current.get(0);
		
		if(e instanceof SheddingEvent) {
			assert !reduceCons;
			reduceCons = true;
		}
		else if(e instanceof RestoreElecEvent){
			assert !restoreCons && !reduceCons;
			restoreCons = true;
		}

	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	throws Exception
	{
		final String uri = this.getURI() ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return "HomeController_ModelReport()" ;
					}

					@Override
					public String getModelURI() {
						return uri ;
					}
			   } ;
	}
	
	public static Architecture build()throws Exception {
		
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		
		atomicModelDescriptors.put(HomeController.URI,
				AtomicModelDescriptor.create(HomeController.class,
						HomeController.URI,
						TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
		
		
		Architecture localArchitecture =
				new Architecture(
						HomeController.URI,
						atomicModelDescriptors,
						new HashMap<>(),
						TimeUnit.SECONDS) ;

		return localArchitecture ;
	}

}
