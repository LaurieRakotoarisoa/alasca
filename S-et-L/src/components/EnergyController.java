package components;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.CarBatteryI;
import interfaces.FridgeI;
import interfaces.TVI;
import ports.carBattery.CarBatteryOutboundPort;
import ports.fridge.FridgeOutboundPort;
import ports.tv.TVOutboundPort;
import utils.BatteryMode;
import utils.FridgeMode;
import utils.TVMode;


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
	
	public void turnOff() throws Exception{
		tvOutbound.turnOff();
		this.logMessage("Etat de la télé : Off");
		
	}
	
	public void turnOn() throws Exception{
		tvOutbound.turnOn();
		this.logMessage("Etat de la télé : ON");
		
	}
	
	public void setBacklight(int backlight) throws Exception{
		tvOutbound.setBacklight(backlight);
		this.logMessage("retro-éclairage de la télé :"+backlight);
		
	}
	
	public void getCarBatteryMode() throws Exception{
		BatteryMode m = batteryOutbound.getState();
		this.logMessage("Etat de la batterie : "+m);
		
	}
	
	public void getTVMode() throws Exception{
		TVMode m = tvOutbound.getState();
		this.logMessage("Etat de la télé : "+m);
		
	}
	
	public void getFridgeMode() throws Exception{
		FridgeMode m = fridgeOutbound.getState();
		this.logMessage("Etat de la réfrigérateur : "+m);
		
	}
	
	public void getLevelCarBattery() throws Exception{
		int lvl = batteryOutbound.getLevelEnergy();
		this.logMessage("Niveau de charge de la batterie de voiture : "+lvl);
	}
	
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
						((EnergyController)this.getTaskOwner()).getCarBatteryMode();
						((EnergyController)this.getTaskOwner()).getFridgeMode();
						((EnergyController)this.getTaskOwner()).getTVMode();
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
						((EnergyController)this.getTaskOwner()).get;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			5000, TimeUnit.MILLISECONDS);
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
