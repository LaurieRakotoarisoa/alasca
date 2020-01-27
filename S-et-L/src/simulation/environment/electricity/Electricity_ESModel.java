package simulation.environment.electricity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.environment.electricity.events.RestoreElecEvent;
import simulation.environment.electricity.events.SheddingEvent;


@ModelExternalEvents (exported = {SheddingEvent.class,RestoreElecEvent.class})
public class Electricity_ESModel 
extends AtomicES_Model{

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public Electricity_ESModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// Constants and Variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Electricity-ES"; 
	
	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;

		// Schedule the first TicEvent.
		Time occurrence = initialTime.add(new Duration(3000, this.getSimulatedTimeUnit())) ;
		Set<ES_EventI> events = new HashSet<ES_EventI>();
		events.add(new SheddingEvent(occurrence));
		events.add(new RestoreElecEvent(occurrence.add(new Duration(2000, getSimulatedTimeUnit()))));
		
//		occurrence = new Time(6000, this.getSimulatedTimeUnit());
//		events.add(new SheddingEvent(occurrence));
//		events.add(new RestoreElecEvent(occurrence.add(new Duration(1000, getSimulatedTimeUnit()))));
		this.scheduleEvents(events) ;
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
					this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
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
						return "Electricity_ModelReport()" ;
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
		
		atomicModelDescriptors.put(Electricity_ESModel.URI,
				AtomicModelDescriptor.create(Electricity_ESModel.class,
						Electricity_ESModel.URI,
						TimeUnit.SECONDS,null,SimulationEngineCreationMode.ATOMIC_ENGINE));
		
		
		Architecture localArchitecture =
				new Architecture(
						Electricity_ESModel.URI,
						atomicModelDescriptors,
						new HashMap<>(),
						TimeUnit.SECONDS) ;

		return localArchitecture ;
	}

}
