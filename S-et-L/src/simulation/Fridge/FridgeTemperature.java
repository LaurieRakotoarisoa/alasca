package simulation.Fridge;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

public class FridgeTemperature
extends AtomicHIOA{

	
	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public FridgeTemperature(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.setDebugLevel(1);
		assert this.temperature != null;
		this.staticInitialiseVariables();
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** evolution mode depending on variable temperature */
	protected Variation_Temperature variationTemp;
	
	public static final String URI = "fridge-temperature";
	
	/** target temperature for the fridge */
	public static final double TARGET_TEMP = 4.0;
	
	/** INITIAL mean temperature */
	public static final double MEAN_TEMP = 4.0;
	
	/** increasing rate when two compressors are running */
	public static double NO_RATE = 0.01 * TARGET_TEMP/MEAN_TEMP;
	
	private static final String	SERIES1 = "Fridge temperature" ;
	
	/** maximum difference between current temperature and target */
	public static final double DIF_LIMIT = 0.5;
	
	/** run parameter to plot the evolution of temperature */
	public static final String FRIDGE_TEMP_PLOTTING_PARAM_NAME = "fridge-temp-plot";
	
	/** Frame used to plot the temperature during the simulation.			*/
	protected XYPlotter			tempPlotter ;

	
	protected enum Variation_Temperature {
		DECREASE, INCREASE
	}
	
	// -------------------------------------------------------------------------
	// HIOA Model Variables
	// -------------------------------------------------------------------------
	
	/** Temperature of the fridge in °C */
	@ExportedVariable(type = Double.class)
	protected final Value<Double> temperature = new Value<Double>(this, TARGET_TEMP,0);
	
	// -------------------------------------------------------------------------
	// Simulation protocol and Related methods
	// -------------------------------------------------------------------------

	@Override
	public Vector<EventI> output() {
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return new Duration(10.0, this.getSimulatedTimeUnit());
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		
		String vname = this.getURI() + ":" +
					FRIDGE_TEMP_PLOTTING_PARAM_NAME;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.tempPlotter = new XYPlotter(pd);
		this.tempPlotter.createSeries(SERIES1) ;
		
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.variationTemp = Variation_Temperature.INCREASE;
		if (this.tempPlotter != null) {
			this.tempPlotter.initialise() ;
			this.tempPlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
		if (this.tempPlotter != null) {
			this.tempPlotter.addData(
				SERIES1,
				initialTime.getSimulatedTime(),
				TARGET_TEMP) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);
		this.temperature.v = TARGET_TEMP;
		assert startTime.equals(this.temperature.time);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.logMessage("intern "+elapsedTime);
		super.userDefinedInternalTransition(elapsedTime) ;
		double delta_t = elapsedTime.getSimulatedDuration() ;
		this.computeNewTemp(this.getCurrentStateTime(),delta_t);
	}
	
	protected void computeNewTemp(Time current, double delta_t) {
		double oldTemp = this.temperature.v;
		double oldTime = this.getCurrentStateTime().getSimulatedTime() - delta_t;
		if(this.variationTemp == Variation_Temperature.INCREASE) {
			this.temperature.v = this.temperature.v + delta_t * NO_RATE;
		}
		else {
			this.temperature.v = this.temperature.v - delta_t * NO_RATE;
		}
		this.temperature.time = current;
		if (this.tempPlotter != null) {
			this.tempPlotter.addData(SERIES1,
							 				current.getSimulatedTime(),
							 				this.temperature.v) ;
		}
		
		//TODO modifier l'emplacement ou supprimer ?
		if(this.temperature.v > MEAN_TEMP+DIF_LIMIT) {
			this.variationTemp = Variation_Temperature.DECREASE;
		}
		else if(this.temperature.v < MEAN_TEMP-DIF_LIMIT) {
			this.variationTemp = Variation_Temperature.INCREASE;
		}
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		final String uri = this.getURI() ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					/**
					 * @see fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI#getModelURI()
					 */
					@Override
					public String getModelURI() { return uri ; }

					/**
					 * @see java.lang.Object#toString()
					 */
					@Override
					public String toString() { return "FridgeTemperature()" ; }
		};
	}

}
