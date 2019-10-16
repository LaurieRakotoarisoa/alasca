package components.controller;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.CarBatteryI;
import interfaces.FridgeI;
import interfaces.TVI;
import ports.carBattery.CarBatteryOutboundPort;
import ports.fridge.FridgeOutboundPort;
import ports.tv.TVOutboundPort;
import components.controller.utilController.*;


@RequiredInterfaces (required = {CarBatteryI.class, TVI.class, FridgeI.class})
public class EnergyController extends AbstractComponent{
	
	protected CarBatteryOutboundPort batteryOutbound;
	protected FridgeOutboundPort fridgeOutbound;
	protected TVOutboundPort tvOutbound;
	
	protected EnergyController(String URI,String CarBatteryoutboundURI, String TVoutboundURI, String FridgeoutboundURI) throws Exception {
		super(URI,1,1 );
		
		batteryOutbound = new CarBatteryOutboundPort(CarBatteryoutboundURI,this);
		batteryOutbound.localPublishPort();
		tvOutbound = new TVOutboundPort(TVoutboundURI,this);
		tvOutbound.localPublishPort();
		fridgeOutbound = new FridgeOutboundPort(FridgeoutboundURI,this);
		fridgeOutbound.localPublishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("energy controller") ;
	}
	
	//TV methode's
	public void tvTurnOff() throws Exception {TV.TurnOff(tvOutbound, this);}
	public void tvTurnOn() throws Exception {TV.TurnOn(tvOutbound, this);}
	public void tvSetBacklight(int backlight) throws Exception {TV.SetBacklight(tvOutbound, this, backlight);}
	public void tvGetMode() throws Exception {TV.getMode(tvOutbound, this);}
	
	//Fridge methode's
	public void fridgeTurnOff() throws Exception {Fridge.TurnOff(fridgeOutbound, this);}
	public void fridgeTurnOn() throws Exception {Fridge.TurnOn(fridgeOutbound, this);}
	public void fridgeSetTemperature(int temperature) throws Exception {Fridge.SetTemperature(fridgeOutbound, this, temperature);}
	public void fridgeGetMode() throws Exception {Fridge.getMode(fridgeOutbound, this);}
	
	//Car Battery methode's
	public void carBatteryTurnOff() throws Exception {CarBattery.getMode(batteryOutbound, this);}
	public void carBatteryGetMode() throws Exception {CarBattery.getBattery(batteryOutbound, this);}
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		this.logMessage("executing controller component.") ;
		// Schedule the first service method invocation in one second.
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((EnergyController)this.getTaskOwner()).carBatteryGetMode();
						((EnergyController)this.getTaskOwner()).fridgeGetMode();
						((EnergyController)this.getTaskOwner()).tvGetMode();
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
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			10000, TimeUnit.MILLISECONDS);
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
		this.batteryOutbound.doDisconnection();
		this.batteryOutbound.unpublishPort() ;
		this.fridgeOutbound.doDisconnection();
		this.fridgeOutbound.unpublishPort() ;
		this.tvOutbound.doDisconnection();
		this.tvOutbound.unpublishPort() ;

		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}
	
	

	


}
