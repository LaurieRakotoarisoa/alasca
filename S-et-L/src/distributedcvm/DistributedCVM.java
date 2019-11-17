package distributedcvm;

import components.compteur.Compteur;
import components.controller.EnergyController;
import components.device.Fridge;
import components.device.Oven;
import components.device.TV;
import components.production.Production;
import components.production.WindTurbine;
import connectors.CompteurConnector;
import connectors.FridgeConnector;
import connectors.OvenConnector;
import connectors.ProductionConnector;
import connectors.TVConnector;
import connectors.TurbineConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import utils.URI;

public class DistributedCVM extends AbstractDistributedCVM{
		
	protected String	ovenURI ;
	protected String	tvURI ;
	protected String	turbineURI ;
	protected String	counterURI ;
	protected String	fridgeURI ;
	protected String	controllerURI ;
	protected String 	productionURI;

	public DistributedCVM(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	public void			initialise() throws Exception
	{

		super.initialise() ;

	}
	
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(URI.OVEN_COMPONENT_URI)) {

			// create the oven component
			this.ovenURI =
					AbstractComponent.createComponent(
							Oven.class.getCanonicalName(),
							new Object[]{URI.OVEN_COMPONENT_URI,
										 URI.OvenInboundPortURI}) ;
			assert	this.isDeployedComponent(this.ovenURI) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.ovenURI) ;
			this.toggleLogging(this.ovenURI) ;
			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI != null
					&& this.productionURI == null;

		} else if (thisJVMURI.equals(URI.TV_COMPONENT_URI)) {

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
			assert	this.turbineURI == null && this.tvURI != null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null
					&& this.productionURI == null;

		} else if (thisJVMURI.equals(URI.WIND_COMPONENT_URI)) {

			// create the Wind Turbine component
			this.turbineURI =
					AbstractComponent.createComponent(
							WindTurbine.class.getCanonicalName(),
							new Object[]{URI.WIND_COMPONENT_URI,
									URI.WindInboundPortURI}) ;
			assert	this.isDeployedComponent(this.turbineURI) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.turbineURI) ;
			this.toggleLogging(this.turbineURI) ;
			assert	this.turbineURI != null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null
					&& this.productionURI == null;

		}else if (thisJVMURI.equals(URI.COUNTER_COMPONENT_URI)) {

			// create counter component
			this.counterURI =
					AbstractComponent.createComponent(
							Compteur.class.getCanonicalName(),
							new Object[]{URI.COUNTER_COMPONENT_URI,
									URI.CounterInboundPortURI}) ;
			assert	this.isDeployedComponent(this.counterURI) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.counterURI) ;
			this.toggleLogging(this.counterURI) ;
			assert	this.turbineURI == null && this.tvURI == null && this.counterURI != null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null
					&& this.productionURI == null;

		}else if (thisJVMURI.equals(URI.Fridge_COMPONENT_URI)) {

			// create the fridge Turbine component
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
			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI != null && this.controllerURI == null && this.ovenURI == null
					&& this.productionURI == null;

		}else if (thisJVMURI.equals(URI.CONTROLLER_COMPONENT_URI)) {

			// create the controller component
			this.controllerURI =
					AbstractComponent.createComponent(
							EnergyController.class.getCanonicalName(),
							new Object[]{URI.CONTROLLER_COMPONENT_URI,
									URI.ProductionOutboundPortURI,
									URI.WindOutboundPortURI,
									URI.CounterOutboundPortURI,
									URI.OvenOutboundPortURI,
									URI.TVOutboundPortURI,
									URI.FridgeOutboundPortURI}) ;
			assert	this.isDeployedComponent(this.controllerURI) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.controllerURI) ;
			this.toggleLogging(this.controllerURI) ;
			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI != null && this.ovenURI == null
					&& this.productionURI == null;

		}
		else if (thisJVMURI.equals(URI.PRODUCTION_COMPONENT_URI)) {

			// create the production component
			this.productionURI =
					AbstractComponent.createComponent(
							Production.class.getCanonicalName(),
							new Object[]{URI.PRODUCTION_COMPONENT_URI,
									URI.ProductionInboundPortURI}) ;
			assert	this.isDeployedComponent(this.productionURI) ;
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			this.toggleTracing(this.productionURI) ;
			this.toggleLogging(this.productionURI) ;
			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null 
					&& this.productionURI != null;

		}
		else{

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.instantiateAndPublish();
	}
	
	@Override
	public void			interconnect() throws Exception
	{
		assert	this.isIntantiatedAndPublished() ;
		
		if (thisJVMURI.equals(URI.OVEN_COMPONENT_URI)) {

			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI != null ;

		} else if (thisJVMURI.equals(URI.TV_COMPONENT_URI)) {

			assert	this.turbineURI == null && this.tvURI != null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null ;

		} else if (thisJVMURI.equals(URI.WIND_COMPONENT_URI)) {

			assert	this.turbineURI != null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null ;

		}else if (thisJVMURI.equals(URI.COUNTER_COMPONENT_URI)) {

			assert	this.turbineURI == null && this.tvURI == null && this.counterURI != null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null ;

		}else if (thisJVMURI.equals(URI.Fridge_COMPONENT_URI)) {

			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI != null && this.controllerURI == null && this.ovenURI == null ;

		}else if (thisJVMURI.equals(URI.CONTROLLER_COMPONENT_URI)) {

			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI != null && this.ovenURI == null ;
			//Connection
			this.doPortConnection(
					this.controllerURI,
					URI.OvenOutboundPortURI,
					URI.OvenInboundPortURI,
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
			this.doPortConnection(
					this.controllerURI,
					URI.ProductionOutboundPortURI,
					URI.ProductionInboundPortURI,
					ProductionConnector.class.getCanonicalName()) ;
			this.doPortConnection(
					this.controllerURI,
					URI.WindOutboundPortURI,
					URI.WindInboundPortURI,
					TurbineConnector.class.getCanonicalName()) ;
			
			// do the connection counter
			this.doPortConnection(
					this.controllerURI,
					URI.CounterOutboundPortURI,
					URI.CounterInboundPortURI,
					CompteurConnector.class.getCanonicalName()) ;

		}
		else if (thisJVMURI.equals(URI.PRODUCTION_COMPONENT_URI)) {
			assert	this.turbineURI == null && this.tvURI == null && this.counterURI == null 
					&& this.fridgeURI == null && this.controllerURI == null && this.ovenURI == null 
					&& this.productionURI != null;

		}
		
		else{

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}
		

		super.interconnect();
	}
	
	public static void	main(String[] args)
	{
		try {
			DistributedCVM da  = new DistributedCVM(args) ;
			da.startStandardLifeCycle(200000000000L) ;
			Thread.sleep(5000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
