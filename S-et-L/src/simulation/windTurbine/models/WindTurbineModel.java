package simulation.windTurbine.models;

import java.util.ArrayList;
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
import simulation.environment.wind.events.ModerateWindEvent;
import simulation.environment.wind.events.WaftEvent;
import simulation.environment.wind.events.WindGustsEvent;


@ModelExternalEvents(imported = {WaftEvent.class,
								ModerateWindEvent.class,
								WindGustsEvent.class})
public class WindTurbineModel 
extends AtomicHIOA{

	public WindTurbineModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.staticInitialiseVariables();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "wind-turbine-model";
	
	/** energy produced when waft wind*/
	private static final double LOW_WIND = 100.0;
	
	/** energy produced when moderate wind in Watt*/
	private static final double MODERATE_WIND = 1000.0;
	
	private static final String	SERIES = "Turbine energy" ;
	public static final String TURBINE_ENERGY_PLOTTING_PARAM_NAME = "turbine-energy-plot";
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter			energyPlotter ;
	
	@ExportedVariable (type = Double.class)
	protected final Value<Double> windEnergy = new Value<Double>(this, 0.0);
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		
		String vname = this.getURI() + ":" +
				TURBINE_ENERGY_PLOTTING_PARAM_NAME ;
		if(simParams.get(vname) != null) {
			PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
			this.energyPlotter = new XYPlotter(pd) ;
			this.energyPlotter.createSeries(SERIES);
		}
	}
	
	@Override
	public void initialiseVariables(Time startTime) {		
		super.initialiseVariables(startTime);
		this.windEnergy.v = 0.0;
		assert startTime.equals(this.windEnergy.time);
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.energyPlotter != null) {
			this.energyPlotter.initialise() ;
			this.energyPlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
		
		if (this.energyPlotter != null) {
			this.energyPlotter.addData(
				SERIES,
				initialTime.getSimulatedTime(),
				this.windEnergy.v) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;
		ArrayList<EventI> current = this.getStoredEventAndReset();
		assert current != null & current.size() == 1;
		EventI e = current.get(0);
		
		if(e instanceof WaftEvent) {
			updateWindEnergy(getCurrentStateTime(), LOW_WIND);
		}
		else if(e instanceof ModerateWindEvent) {
			updateWindEnergy(getCurrentStateTime(), MODERATE_WIND);
		}
		else if (e instanceof WindGustsEvent) {
			updateWindEnergy(getCurrentStateTime(), 0.0);
		}
	}

	@Override
	public ArrayList<EventI> output() {
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return Duration.INFINITY;
	}
	
	private void updateWindEnergy(Time current, double energy) {
		this.windEnergy.v = energy;
		this.windEnergy.time = current;
		
		if (this.energyPlotter != null) {
			this.energyPlotter.addData(
				SERIES,
				current.getSimulatedTime(),
				this.windEnergy.v) ;
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
						return "WindTurbine_ModelReport()" ;
					}

					@Override
					public String getModelURI() {
						return uri ;
					}
			   } ;
	}

}
