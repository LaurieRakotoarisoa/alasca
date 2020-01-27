package simulation.Fridge.models2;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.Fridge.events.ActiveCompressor;
import simulation.Fridge.events.FridgeConsumptionEvent;
import simulation.Fridge.events.InactiveCompressor;

@ModelExternalEvents( imported = {InactiveCompressor.class,
								ActiveCompressor.class},
					exported = { FridgeConsumptionEvent.class})
public class FridgeConsumption 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------


	public FridgeConsumption(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.consumption = DEFAULT_CONS;
		this.triggerUpdate = false;
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "fridge-consumption";
	
	public static final double DEFAULT_CONS = 150.0;
	public static final double LIGHT_CONS = 20.0;
	public static final double HIGH_CONS = 300.0;
	private double consumption;
	
	/** run parameter to plot the evolution of temperature */
	public static final String FRIDGE_CONS_PLOTTING_PARAM_NAME = "fridge-temp-plot";
	
	/** Frame used to plot the temperature during the simulation.			*/
	protected XYPlotter			consPlotter ;
	
	private static final String	SERIES = "Fridge consumption" ;
	
	private boolean triggerUpdate;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		
		String vname = this.getURI() + ":" +
					FRIDGE_CONS_PLOTTING_PARAM_NAME;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.consPlotter = new XYPlotter(pd);
		this.consPlotter.createSeries(SERIES) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.consPlotter != null) {
			this.consPlotter.initialise() ;
			this.consPlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
		if (this.consPlotter != null) {
			this.consPlotter.addData(
				SERIES,
				initialTime.getSimulatedTime(),
				this.consumption) ;
		}
	}
	
	@Override
	public Vector<EventI> output() {
		if(this.triggerUpdate) {
			Vector<EventI> ret = new Vector<EventI>();
			Time t = this.getCurrentStateTime().add(getNextTimeAdvance());
			ret.add(new FridgeConsumptionEvent(t, consumption));
			this.triggerUpdate = false;
			return ret;
		}	
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if(this.triggerUpdate) return Duration.zero(TimeUnit.SECONDS);
		return Duration.INFINITY;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;
		Vector<EventI> current = this.getStoredEventAndReset();
		assert current != null & current.size() == 1;
		EventI e = current.get(0);
		
		if(e instanceof ActiveCompressor) {
			ActiveCompressor event = (ActiveCompressor) e;
			if(event.isDoorOpened()) {
				if(event.isEcoModeActivated()) 
				{ this.consumption = DEFAULT_CONS;}
				
				else 
				{ this.consumption = HIGH_CONS; }
			}
			else {
				if(event.isEcoModeActivated()) this.consumption = DEFAULT_CONS*0.9;
				else this.consumption = DEFAULT_CONS;
			}
			
		}
		else if(e instanceof InactiveCompressor) {
			InactiveCompressor event = (InactiveCompressor) e;
			if(event.isDoorOpened()) {
				this.consumption = LIGHT_CONS;
			}
			else {
				this.consumption = 0.0;
			}
		}
		
		if (this.consPlotter != null) {
			this.consPlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				this.consumption) ;
		}
		
		this.triggerUpdate = true;
		
	}
	
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		final String uri = this.uri ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;
					@Override
					public String getModelURI() {
						return uri ;
					}				
				};
	}

}
