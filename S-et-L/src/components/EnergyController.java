package components;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.CarBatteryI;
import ports.CarBatteryOutboundPort;
import utils.BatteryMode;


@RequiredInterfaces (required = CarBatteryI.class)
public class EnergyController extends AbstractComponent{
	
	protected CarBatteryOutboundPort batteryOutbound;
	
	protected EnergyController(String URI,String outboundURI) throws Exception {
		super(URI,1, 1);
		
		batteryOutbound = new CarBatteryOutboundPort(outboundURI,this);
		batteryOutbound.localPublishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("energy controller") ;
	}
	
	public void getCarBatteryMode() throws Exception{
		BatteryMode m = batteryOutbound.getState();
		this.logMessage("Etat de la batterie : "+m);
		
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
						((EnergyController)this.getTaskOwner()).getCarBatteryMode() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			},
			1000, TimeUnit.MILLISECONDS);
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

		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}
	
	

	


}
