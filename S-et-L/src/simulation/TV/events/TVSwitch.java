package simulation.TV.events;

import clean.equipments.tv.mil.models.TVStateMILModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.time.Time;


/**
 * The class <code>TVSwitch</code> describes the event turning on the TV 
 * @author laurie
 *
 */
public class TVSwitch 
extends ES_Event{

	public TVSwitch(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String		eventAsString()
	{
		return "TVSwitch(" +
						this.getTimeOfOccurrence().getSimulatedTime() +" "+this.getTimeOfOccurrence().getTimeUnit()+ ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		assert model instanceof TVStateMILModel;
		TVStateMILModel tv = (TVStateMILModel) model;
		tv.switchState(timeOfOccurrence.getSimulatedTime());	
	}

}
