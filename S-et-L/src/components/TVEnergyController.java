package components;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import interfaces.TVI;
import ports.TVOutboundPort;
import utils.TVMode;

@RequiredInterfaces (required = TVI.class)
public class TVEnergyController extends AbstractComponent{
	
	protected TVOutboundPort tvOutbound;
	
	protected TVEnergyController(String URI,String outboundURI) throws Exception {
		super(URI,1, 1);
		
		tvOutbound = new TVOutboundPort(outboundURI,this);
		tvOutbound.localPublishPort();
		this.executionLog.setDirectory(System.getProperty("user.home")) ;
		this.tracer.setTitle("TV energy controller") ;
	}
	
	public void getTVMode() throws Exception{
		TVMode m = tvOutbound.getState();
		this.logMessage("Etat de la télé : "+m);
		
	}
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		this.logMessage("executing TV controller component.") ;
		// Schedule the first service method invocation in one second.
		this.scheduleTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						((TVEnergyController)this.getTaskOwner()).getTVMode();
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
		this.logMessage("stopping TV controller component.") ;
		this.printExecutionLogOnFile("controller");
		// This is the place where to clean up resources, such as
		// disconnecting and unpublishing ports that will be destroyed
		// when shutting down.
		// In static architectures like in this example, ports can also
		// be disconnected by the finalise method of the component
		// virtual machine.
		this.tvOutbound.doDisconnection();
		this.tvOutbound.unpublishPort() ;

		// This called at the end to make the component internal
		// state move to the finalised state.
		super.finalise();
	}

}
