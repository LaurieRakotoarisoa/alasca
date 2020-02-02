package clean.equipments.oven.sil.events;

import clean.equipments.oven.sil.OvenStateSILCoupledModel;
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
		if(model instanceof OvenStateSILCoupledModel) {
			OvenStateSILCoupledModel oven = (OvenStateSILCoupledModel) model;
			if(oven.isEcoActivated()) oven.deactivateEnergyEco();
			assert !oven.isEcoActivated();
		}
	}

}
