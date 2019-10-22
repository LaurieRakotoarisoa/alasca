package ports.compteur;

import components.compteur.Compteur;
import components.device.TV;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.CompteurI;

/**
 *	The class <code>CompteurInboudPort</code> defines the inbound port
 * exposing the interface <code>CompteurI</code> for components of
 * type <code>Compteur</code>.
 * 
 * @author Saad CHIADMI
 *
 */
public class CompteurInboudPort extends AbstractInboundPort
			implements CompteurI{
	
	public CompteurInboudPort(String uri,ComponentI owner) throws Exception {
		super(uri, CompteurI.class, owner);
		assert owner instanceof TV;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see interfaces.CompteurI#getConsumptionOfAllDevices()
	 */
	@Override
	public int getConsumptionOfAllDevices() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Compteur)owner).getCons());
	}
	
}
