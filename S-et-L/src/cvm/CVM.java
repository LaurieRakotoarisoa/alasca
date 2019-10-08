package cvm;

import components.CarBattery;
import components.EnergyController;
import connectors.CarBatteryConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM{
	
	/** URI of the provider component (convenience).						*/
	protected static final String	CONTROLLER_COMPONENT_URI = "my-URI-controller" ;
	/** URI of the consumer component (convenience).						*/
	protected static final String	BATTERY_COMPONENT_URI = "my-URI-battery" ;
	/** URI of the provider outbound port (simplifies the connection).	*/
	protected static final String	BatteryOutboundPortURI = "oport" ;
	/** URI of the consumer inbound port (simplifies the connection).		*/
	protected static final String	BatteryInboundPortURI = "iport" ;
	
	protected String controllerURI;
	protected String carBatteryURI;

	public CVM() throws Exception {
		super();
	}
	
	@Override
	public void			deploy() throws Exception
	{
		assert	!this.deploymentDone() ;

		// --------------------------------------------------------------------
		// Configuration phase
		// --------------------------------------------------------------------

		// debugging mode configuration; comment and uncomment the line to see
		// the difference
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PUBLIHSING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING) ;
//		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.COMPONENT_DEPLOYMENT) ;

		// --------------------------------------------------------------------
		// Creation phase
		// --------------------------------------------------------------------

		// create the battery component
		this.carBatteryURI =
			AbstractComponent.createComponent(
					CarBattery.class.getCanonicalName(),
					new Object[]{BATTERY_COMPONENT_URI,
							BatteryInboundPortURI}) ;
		assert	this.isDeployedComponent(this.carBatteryURI) ;
		// make it trace its operations; comment and uncomment the line to see
		// the difference
		this.toggleTracing(this.carBatteryURI) ;
		this.toggleLogging(this.carBatteryURI) ;

		// create the controller component
		this.controllerURI =
			AbstractComponent.createComponent(
					EnergyController.class.getCanonicalName(),
					new Object[]{CONTROLLER_COMPONENT_URI,
							BatteryOutboundPortURI}) ;
		assert	this.isDeployedComponent(this.controllerURI) ;
		// make it trace its operations; comment and uncomment the line to see
		// the difference
		this.toggleTracing(this.controllerURI) ;
		this.toggleLogging(this.controllerURI) ;
		
		// --------------------------------------------------------------------
		// Connection phase
		// --------------------------------------------------------------------

		// do the connection
		this.doPortConnection(
				this.controllerURI,
				BatteryOutboundPortURI,
				BatteryInboundPortURI,
				CarBatteryConnector.class.getCanonicalName()) ;
		// Nota: the above use of the reference to the object representing
		// the URI consumer component is allowed only in the deployment
		// phase of the component virtual machine (to perform the static
		// interconnection of components in a static architecture) and
		// inside the concerned component (i.e., where the method
		// doPortConnection can be called with the this destination
		// (this.doPortConenction(...)). It must never be used in another
		// component as the references to objects used to implement component
		// features must not be shared among components.

		// --------------------------------------------------------------------
		// Deployment done
		// --------------------------------------------------------------------

		super.deploy();
		assert	this.deploymentDone() ;
	}
	
	@Override
	public void				finalise() throws Exception
	{
		super.finalise();
	}
	
	@Override
	public void				shutdown() throws Exception
	{
		assert	this.allFinalised() ;
		// any disconnection not done yet can be performed here

		super.shutdown();
	}

	public static void		main(String[] args)
	{
		try {
			// Create an instance of the defined component virtual machine.
			CVM a = new CVM() ;
			// Execute the application.
			a.startStandardLifeCycle(20000L) ;
			// Give some time to see the traces (convenience).
			Thread.sleep(5000L) ;
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
