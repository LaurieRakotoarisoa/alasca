package components.compteur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.ports.PortI;
import interfaces.CompteurI;
import ports.compteur.CompteurInboudPort;

/**
 * The class <code>Compteur</code> implements a component that models a Compteur's behavior
 * @author Saad CHIADMI
 *
 */
@OfferedInterfaces (offered = CompteurI.class)
public class Compteur extends AbstractComponent{
	
	/**
	 * addition of all consumption
	 */
	protected int cons = 0;
	
	/**
	 * @param URI Component uri
	 * @param inboundURI uri for the inbound port 
	 * @throws Exception
	 */
	protected Compteur(String URI,String inboundURI) throws Exception {
		super(URI,1, 0);
		
		//Create and publish port for remote control
		PortI compteurInboundPort = new CompteurInboudPort(URI, this);
		compteurInboundPort.publishPort();
		this.executionLog.setDirectory(System.getProperty("user.home"));
		this.tracer.setTitle("Compteur");
	}
	
	/**
	 * <p>Give information about the current consumption of all devices</p>
	 * @return {@link integer}
	 */
	public int getCons() {
		return cons;
	}
	
	public int setCons(int cons) {
		this.cons = cons;
		return cons;
	}

}
