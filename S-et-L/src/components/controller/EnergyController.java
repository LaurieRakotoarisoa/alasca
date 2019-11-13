package components.controller;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.OvenI;
import interfaces.ProductionI;
import interfaces.CompteurI;
import interfaces.FridgeI;
import interfaces.TVI;
import interfaces.WindTurbineI;
import ports.compteur.CompteurOutboundPort;
import ports.fridge.FridgeOutboundPort;
import ports.oven.OvenOutboundPort;
import ports.production.ProductionOutboundPort;
import ports.turbine.TurbineOutboundPort;
import ports.tv.TVOutboundPort;
import components.controller.utilController.*;


@RequiredInterfaces (required = {OvenI.class, TVI.class, FridgeI.class, CompteurI.class, ProductionI.class, WindTurbineI.class})
public class EnergyController extends AbstractComponent{
	
	protected OvenOutboundPort ovenOutbound;
	protected FridgeOutboundPort fridgeOutbound;
	protected TVOutboundPort tvOutbound;
	protected CompteurOutboundPort counterOutbound;
	protected ProductionOutboundPort productionOutbound;
	protected TurbineOutboundPort windOutbound;
	
	protected EnergyController(String URI, String productionOutboundURI, String windOutboundURI, String counterOutboundURI, String ovenOutboundURI, String TVOutboundURI, String FridgeOutboundURI) throws Exception {
		super(URI,1,1 );
		
		ovenOutbound = new OvenOutboundPort(ovenOutboundURI,this);
		ovenOutbound.localPublishPort();
		tvOutbound = new TVOutboundPort(TVOutboundURI,this);
		tvOutbound.localPublishPort();
		fridgeOutbound = new FridgeOutboundPort(FridgeOutboundURI,this);
		fridgeOutbound.localPublishPort();
		counterOutbound = new CompteurOutboundPort(counterOutboundURI,this);
		counterOutbound.localPublishPort();
		productionOutbound = new ProductionOutboundPort(productionOutboundURI,this);
		productionOutbound.localPublishPort();
		windOutbound = new TurbineOutboundPort(windOutboundURI,this);
		windOutbound.localPublishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("energy controller") ;
	}
	
	//TV methode's
	public void tvTurnOff() throws Exception {TV.turnOff(tvOutbound, this);}
	public void tvTurnOn() throws Exception {TV.turnOn(tvOutbound, this);}
	public void tvSetBacklight(int backlight) throws Exception {TV.setBacklight(tvOutbound, this, backlight);}
	public void tvGetMode() throws Exception {TV.getMode(tvOutbound, this);}
	
	//Fridge methode's
	public void fridgeTurnOff() throws Exception {Fridge.turnOff(fridgeOutbound, this);}
	public void fridgeTurnOn() throws Exception {Fridge.turnOn(fridgeOutbound, this);}
	public void fridgeSetTemperature(int temperature) throws Exception {Fridge.setTemperature(fridgeOutbound, this, temperature);}
	public void fridgeGetMode() throws Exception {Fridge.getMode(fridgeOutbound, this);}
	
	//Oven methode's
	public void ovenGetMode() throws Exception {Oven.getMode(ovenOutbound, this);}
	public void ovenTurnOff() throws Exception {Oven.turnOff(ovenOutbound, this);}
	public void ovenTurnOn() throws Exception {Oven.turnOn(ovenOutbound, this);}
	public void ovenSetTemperature(int temperature) throws Exception {Oven.setTemperature(ovenOutbound, this, temperature);}
	public void ovenTurnOnInDate(LocalDateTime date, int temperature) throws Exception {Oven.displayDate(ovenOutbound, this, date, temperature);}
	public void ovenTurnOnSetTemperatur(int temperature) throws Exception {Oven.turnOnIn(ovenOutbound, this, temperature);}
	
	//Counter
	public int getAllCons() throws Exception{
		int cons = Oven.getCons(ovenOutbound, this)+Fridge.getCons(fridgeOutbound, this)+TV.getCons(tvOutbound, this);
		this.logMessage("La consomation d'énergie acctuel est "+cons+" Watt");
		return cons;
	}
	
	//Production
	public void getProduction() throws Exception{Production.getProduction(productionOutbound, this);}
	public void setProduction(int cons) throws Exception{Production.setProduction(cons, productionOutbound, this);}
	
	
	//Wind turbine methode's
	public void windGetMode() throws Exception {Wind.getMode(windOutbound, this);}
	public void windTurnOn() throws Exception {Wind.turnOn(windOutbound, this);}
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		this.logMessage("executing controller component.") ;
		// Schedule the first service method invocation in one second.
		
//		this.scheduleTask(
//				new AbstractComponent.AbstractTask() {
//					@Override
//					public void run() {
//						try {
//							while(true) {
//								Thread.sleep(2000);
//								((EnergyController)this.getTaskOwner()).getAllCons();
//							}
//						} catch (Exception e) {
//							throw new RuntimeException(e) ;
//						}
//					}
//				},
//				1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((EnergyController)this.getTaskOwner()).windTurnOn();
						((EnergyController)this.getTaskOwner()).ovenGetMode();
						((EnergyController)this.getTaskOwner()).fridgeGetMode();
						((EnergyController)this.getTaskOwner()).tvGetMode();
						((EnergyController)this.getTaskOwner()).getAllCons();
						((EnergyController)this.getTaskOwner()).setProduction(getAllCons());
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			1000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((EnergyController)this.getTaskOwner()).tvTurnOn();
						((EnergyController)this.getTaskOwner()).tvSetBacklight(60);
						((EnergyController)this.getTaskOwner()).fridgeTurnOn();
						((EnergyController)this.getTaskOwner()).fridgeSetTemperature(4);
						((EnergyController)this.getTaskOwner()).getAllCons();
						((EnergyController)this.getTaskOwner()).setProduction(getAllCons());
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			10000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((EnergyController)this.getTaskOwner()).ovenTurnOn();
							((EnergyController)this.getTaskOwner()).ovenSetTemperature(170);
							((EnergyController)this.getTaskOwner()).getAllCons();
							((EnergyController)this.getTaskOwner()).setProduction(getAllCons());
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				20000, TimeUnit.MILLISECONDS);
		
		this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((EnergyController)this.getTaskOwner()).ovenTurnOff();
							((EnergyController)this.getTaskOwner()).tvTurnOff();
							((EnergyController)this.getTaskOwner()).fridgeTurnOff();
							((EnergyController)this.getTaskOwner()).getAllCons();
							((EnergyController)this.getTaskOwner()).ovenTurnOnInDate(LocalDateTime.now().plusSeconds(10), 190);
							while(true) {
								if(Oven.dateToOn.isEqual(LocalDateTime.now().minusNanos(LocalDateTime.now().getNano()))) {
									((EnergyController)this.getTaskOwner()).ovenTurnOnSetTemperatur(Oven.temperatureToOn);
									((EnergyController)this.getTaskOwner()).getAllCons();
									((EnergyController)this.getTaskOwner()).setProduction(getAllCons());
									break;
								}
							}
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				25000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void			finalise() throws Exception
	{
		this.logMessage("stopping controller component.") ;
		this.printExecutionLogOnFile("controller");
		// This is the place where to clean up resources, such as
		// disconnecting and unpublishing ports that will be destroyed
		// when shutting down.
		// In static architectures like in this example, ports can also
		// be disconnected by the finalise method of the component
		// virtual machine.
		this.ovenOutbound.doDisconnection();
		this.ovenOutbound.unpublishPort() ;
		this.fridgeOutbound.doDisconnection();
		this.fridgeOutbound.unpublishPort() ;
		this.tvOutbound.doDisconnection();
		this.tvOutbound.unpublishPort() ;
		this.productionOutbound.doDisconnection();
		this.productionOutbound.unpublishPort() ;
		this.windOutbound.doDisconnection();
		this.windOutbound.unpublishPort() ;
//		this.counterOutbound.doDisconnection();
//		this.counterOutbound.unpublishPort() ;

		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}
	
	

	


}
