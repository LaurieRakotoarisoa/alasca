package cvm;

import components.controller.EnergyController;
import components.device.Oven;
import components.device.Fridge;
import components.device.TV;
import connectors.OvenConnector;
import connectors.FridgeConnector;
import connectors.TVConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import utils.URI;

public class CVM extends AbstractCVM{
	
	protected String controllerURI;
	protected String carBatteryURI;
	protected String fridgeURI;
	protected String tvURI;

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
					Oven.class.getCanonicalName(),
					new Object[]{URI.BATTERY_COMPONENT_URI,
							URI.BatteryInboundPortURI}) ;
		assert	this.isDeployedComponent(this.carBatteryURI) ;
		// make it trace its operations; comment and uncomment the line to see
		// the difference
		this.toggleTracing(this.carBatteryURI) ;
		this.toggleLogging(this.carBatteryURI) ;
		
		
		// create the Fridge component
		this.fridgeURI =
				AbstractComponent.createComponent(
						Fridge.class.getCanonicalName(),
						new Object[]{URI.Fridge_COMPONENT_URI,
								URI.FridgeInboundPortURI}) ;
		assert	this.isDeployedComponent(this.fridgeURI) ;
		// make it trace its operations; comment and uncomment the line to see
		// the difference
		this.toggleTracing(this.fridgeURI) ;
		this.toggleLogging(this.fridgeURI) ;
		
		// create the TV component
		this.tvURI =
				AbstractComponent.createComponent(
						TV.class.getCanonicalName(),
						new Object[]{URI.TV_COMPONENT_URI,
								URI.TVInboundPortURI}) ;
		assert	this.isDeployedComponent(this.tvURI) ;
		// make it trace its operations; comment and uncomment the line to see
		// the difference
		this.toggleTracing(this.tvURI) ;
		this.toggleLogging(this.tvURI) ;

		
		// create the controller component
		this.controllerURI =
			AbstractComponent.createComponent(
					EnergyController.class.getCanonicalName(),
					new Object[]{URI.CONTROLLER_COMPONENT_URI,
							URI.BatteryOutboundPortURI,
							URI.TVOutboundPortURI,
							URI.FridgeOutboundPortURI}) ;
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
				URI.BatteryOutboundPortURI,
				URI.BatteryInboundPortURI,
				OvenConnector.class.getCanonicalName()) ;
		this.doPortConnection(
				this.controllerURI,
				URI.FridgeOutboundPortURI,
				URI.FridgeInboundPortURI,
				FridgeConnector.class.getCanonicalName()) ;
		this.doPortConnection(
				this.controllerURI,
				URI.TVOutboundPortURI,
				URI.TVInboundPortURI,
				TVConnector.class.getCanonicalName()) ;
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
