package simulation.environment.wind.models;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import simulation.environment.wind.events.ModerateWindEvent;
import simulation.environment.wind.events.WaftEvent;
import simulation.environment.wind.events.WindGustsEvent;

@ModelExternalEvents(exported = {WaftEvent.class,
								ModerateWindEvent.class,
								WindGustsEvent.class})
public class WindModel 
extends AtomicES_Model{

	public WindModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		rg = new RandomDataGenerator();
		lastEvent = new WaftEvent(new Time(rg.nextUniform(0, 10),TimeUnit.SECONDS));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "wind-model";
	private RandomDataGenerator rg;
	private ES_Event lastEvent;
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		rg.reSeed();
		super.initialiseState(initialTime) ;
		this.scheduleEvent(lastEvent);
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;
		Time t = this.getCurrentStateTime().add(new Duration(rg.nextUniform(30, 500,true),TimeUnit.SECONDS));
		ES_Event next = generateNewWindEvent(t);
		this.scheduleEvent(next);
	}
	
	private ES_Event generateNewWindEvent(Time occurence) {
		if(lastEvent instanceof WaftEvent)
		{
			lastEvent = new ModerateWindEvent(occurence);	
		}
		else if(lastEvent instanceof ModerateWindEvent) {
			lastEvent = new WindGustsEvent(occurence);
		}
		else if(lastEvent instanceof WindGustsEvent) {
			lastEvent = new WaftEvent(occurence);
		}
		
		return lastEvent;
		
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
						return "Wind_ModelReport()" ;
					}

					@Override
					public String getModelURI() {
						return uri ;
					}
			   } ;
	}

}
