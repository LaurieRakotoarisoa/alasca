package ports.compteur;

import components.controller.EnergyController;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.CompteurI;

public class CompteurOutboundPort extends AbstractOutboundPort
implements CompteurI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri,CompteurI.class, owner);
		
		assert owner instanceof EnergyController;
	}

	@Override
	public int getConsumptionOfAllDevices() throws Exception {
		return ((CompteurI)this.connector).getConsumptionOfAllDevices();
	}

	@Override
	public void setConsumptionOfAllDevices(int cons) throws Exception {
		((CompteurI)this.connector).setConsumptionOfAllDevices(cons);
	}

}
