package simulation2.oven.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation2.oven.events.OvenConsumptionEvent;

@ModelExternalEvents(imported = TicEvent.class,
					exported = OvenConsumptionEvent.class)
public class OvenConsumptionModel 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	
	public static class OvenConsumptionModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<OvenConsumptionEvent>	readings ;

		public			OvenConsumptionModelReport(
			String modelURI,
			Vector<OvenConsumptionEvent> readings
			)
		{
			super(modelURI) ;
			this.readings = readings ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "Oven Consumption Model Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of consumption = " + this.readings.size() + "\n" ;
			ret += "Consumptions :\n" ;
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public OvenConsumptionModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		consumptions = new Vector<OvenConsumptionEvent>();
		this.updateConsumption = false;
		this.rgConsumption = new RandomDataGenerator();
		this.lastConsumption = 0.0;
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "Oven-CONSUMPTION";
	
	public static final String OVENCONS_PLOTTING_PARAM_NAME = "oven-cons-plot";
	
	private static final String	SERIES_COMSUMPTION = "OVEN consumption" ;
	
	/** stored output events for report */
	protected Vector<OvenConsumptionEvent> consumptions;
	
	/** true when oven consumption must be updated */
	protected boolean updateConsumption;
	
	protected double lastConsumption;
	
	protected double lastTimeEmitCons;
	
	/** random generator for consumption depending on rate temperature parameter */
	protected final RandomDataGenerator rgConsumption;
	
	/** minimum factor to generate consumption depending on temperature */
	protected final double MIN_RATE_BL = 50.0;
	
	/** maximum factor to generate consumption depending on temperature */
	protected final double MAX_RATE_BL = 100.0;
	
	protected XYPlotter			consPlotter ;
	
	// -------------------------------------------------------------------------
	// HIOA Model Variables
	// -------------------------------------------------------------------------
	
	@ImportedVariable (type = Double.class)
	protected Value<Double> temperature;
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		
		String vname = this.getURI() + ":" +
				OVENCONS_PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.consPlotter = new XYPlotter(pd) ;
		this.consPlotter.createSeries(SERIES_COMSUMPTION) ;
	
	}
	
	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = new Vector<EventI>();
		if(updateConsumption) {
			
			//update current consumption
			double newConsumption = generateConsumption();
			
			// Plotting
			if (this.consPlotter != null) {
				this.consPlotter.addData(
						SERIES_COMSUMPTION,
						this.lastTimeEmitCons,
						newConsumption) ;
				this.consPlotter.addData(
						SERIES_COMSUMPTION,
						this.getCurrentStateTime().getSimulatedTime(),
						newConsumption) ;
			}
			
			//Memorise last consumption
			this.lastConsumption = newConsumption;
			this.lastTimeEmitCons = this.getCurrentStateTime().getSimulatedTime();
			
			
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
			OvenConsumptionEvent e = new OvenConsumptionEvent(t, newConsumption);
			ret.add(e);
			consumptions.add(e);
			updateConsumption = false;
			return ret;
		}
		return null;
	}

	@Override
	public Duration timeAdvance() {
		if (this.updateConsumption) {
			// immediate internal event when a reading is triggered.
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.rgConsumption.reSeed();
		this.lastTimeEmitCons = initialTime.getSimulatedTime();
		this.consumptions.clear();
		if (this.consPlotter != null) {
			this.consPlotter.initialise() ;
			this.consPlotter.showPlotter() ;
		}
		
		super.initialiseState(initialTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		Vector<EventI> current = this.getStoredEventAndReset() ;
		boolean	ticReceived = false ;
		for (int i = 0 ; !ticReceived && i < current.size() ; i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true ;
			}
		}
		if (ticReceived) {
			this.updateConsumption = true;
		}
	}
	
	/**
	 * generate Oven consumption as double value depending on oven temperature
	 * @return Oven consumption as double value
	 */
	public double generateConsumption() {
		double rateConsumption = rgConsumption.nextUniform(MIN_RATE_BL, MAX_RATE_BL);
		assert rateConsumption > MIN_RATE_BL && rateConsumption < MAX_RATE_BL;
		double newConsumption = this.temperature.v * rateConsumption; 
		return newConsumption;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new OvenConsumptionModelReport(this.getURI(),consumptions);
	}
}
