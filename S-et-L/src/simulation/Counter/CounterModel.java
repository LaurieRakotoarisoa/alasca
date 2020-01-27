package simulation.Counter;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.Fridge.events.FridgeConsumptionEvent;
import simulation.TV.events.TVConsumptionEvent;
import utils.events.ConsumptionEventI;


@ModelExternalEvents( imported = { FridgeConsumptionEvent.class,
								TVConsumptionEvent.class} )
public class CounterModel 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CounterModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.consumptions = new HashMap<Class <? extends EventI>,Double>();
		assert this.totalCons != null;
		this.staticInitialiseVariables();
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = "counter-model";
	
	private Map<Class <? extends EventI>,Double> consumptions;
	
	private static final String	SERIES = "Home consumption" ;
	
	public static final String HOMECONS_PLOTTING_PARAM_NAME = "home-cons-plot";
	
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter			homeConsPlotter ;
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------
	
	@ExportedVariable (type = Double.class)
	protected final Value<Double> totalCons = new Value<Double>(this, 0.0);
	
	// -------------------------------------------------------------------------
	// Simulation methods and protocol
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
				HOMECONS_PLOTTING_PARAM_NAME ;
		if(simParams.get(vname) != null) {
			PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
			this.homeConsPlotter = new XYPlotter(pd) ;
			this.homeConsPlotter.createSeries(SERIES) ;
		}
	
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);
		this.totalCons.v = 0.0;
		assert	startTime.equals(this.totalCons.time) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime) {
		
		if (this.homeConsPlotter != null) {
			this.homeConsPlotter.initialise() ;
			this.homeConsPlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
		if (this.homeConsPlotter != null) {
			this.homeConsPlotter.addData(
				SERIES,
				initialTime.getSimulatedTime(),
				this.totalCons.v) ;
		}
		
		
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
		if(e instanceof ConsumptionEventI) {
			consumptions.put(e.getClass(), ((ConsumptionEventI)e).getConsumption());
			
		}
		computeTotalConsumption(this.getCurrentStateTime());
		
	}

	@Override
	public Vector<EventI> output() {
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return Duration.INFINITY;
	}
	
	private void computeTotalConsumption(Time current) {
		this.totalCons.v =  consumptions.entrySet().stream()
		.map(e -> e.getValue())
		.reduce(0.0,(sum,cons) -> sum+cons);
		this.totalCons.time = current;
		
		if (this.homeConsPlotter != null) {
			this.homeConsPlotter.addData(
				SERIES,
				current.getSimulatedTime(),
				this.totalCons.v) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	throws Exception
	{
		final String uri = this.getURI() ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return "Counter_ModelReport()" ;
					}

					@Override
					public String getModelURI() {
						return uri ;
					}
			   } ;
	}

}
