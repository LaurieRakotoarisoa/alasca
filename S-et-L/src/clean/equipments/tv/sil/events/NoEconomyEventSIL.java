package clean.equipments.tv.sil.events;

import clean.equipments.tv.mil.models.TVStateMILModel;
import clean.equipments.tv.sil.TVStateSILModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.Controller.events.NoEconomyEvent;

public class NoEconomyEventSIL 
extends NoEconomyEvent{

	public NoEconomyEventSIL(Time timeOfOccurrence) {
		super(timeOfOccurrence);
	}

	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "NoEconomyEventSIL(" + this.getTimeOfOccurrence().getSimulatedTime()
					+ ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void			executeOn(AtomicModel model)
	{
		if(model instanceof TVStateSILModel) {
			TVStateSILModel tv = (TVStateSILModel) model;
			if(tv.isEcoActivated()) tv.deactivateEnergyEco();
			assert !tv.isEcoActivated();
		}
	}

}
