package simulation.models.oven;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
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
import simulation.models.oven.events.PowerReading;

/**
 * 
 * @author Laurie Rakotoarisoa
 *
 */


@ModelExternalEvents ( imported = {TicEvent.class},
						exported = {PowerReading.class})
public class ElectricPowerSensorModel 
extends AtomicHIOAwithEquations{
	
	// -------------------------------------------------------------------------
		// Inner classes
		// -------------------------------------------------------------------------

		/**
		 * The class <code>ElectricPowerSensorReport</code> implements the
		 * simulation report for the Power sensor model.
		 * 
		 * <pre>
		 * invariant	true
		 * </pre>
		 * 
		 * 
		 * @author	Laurie Rakotoarisoa
		 */
		public static class	ElectricPowerSensorReport
		extends		AbstractSimulationReport
		{
			private static final long 					serialVersionUID = 1L ;
			public final Vector<PowerReading>	readings ;

			public			ElectricPowerSensorReport(
				String modelURI,
				Vector<PowerReading> readings
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
				ret += "Electric Power Sensor Report\n" ;
				ret += "-----------------------------------------\n" ;
				ret += "number of readings = " + this.readings.size() + "\n" ;
				ret += "Readings:\n" ;
				for (int i = 0 ; i < this.readings.size() ; i++) {
					ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
				}
				ret += "-----------------------------------------\n" ;
				return ret ;
			}
		}
		
		// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		private static final long		serialVersionUID = 1L ;
		private static final String		SERIES = "power" ;
		public static final String		URI = "ElectricPowerSensorModel-1" ;
		
		/** true when a external event triggered a reading.						*/
		protected boolean								triggerReading ;
		/** the last value emitted as a reading of the bandwidth.			 	*/
		protected double								lastReading ;
		/** the simulation time at the last reading.							*/
		protected double								lastReadingTime ;
		/** history of readings, for the simulation report.						*/
		protected final Vector<PowerReading>	readings ;

		/** frame used to plot the bandwidth readings during the simulation.	*/
		protected XYPlotter				plotter ;
		
		// -------------------------------------------------------------------------
		// HIOA model variables
		// -------------------------------------------------------------------------

		/** Electric power in Watt (joule per second).								*/
		@ImportedVariable(type = Double.class)
		protected Value<Double>							power ;
		
		
		/**
		 * create an instance of the electric power sensor model.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	simulatedTimeUnit != null
		 * pre	simulationEngine == null ||
		 * 		    	simulationEngine instanceof HIOA_AtomicEngine
		 * post	this.getURI() != null
		 * post	uri != null implies this.getURI().equals(uri)
		 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
		 * post	simulationEngine != null implies
		 * 			this.getSimulationEngine().equals(simulationEngine)
		 * </pre>
		 *
		 * @param uri					unique identifier of the model.
		 * @param simulatedTimeUnit		time unit used for the simulation clock.
		 * @param simulationEngine		simulation engine enacting the model.
		 * @throws Exception			<i>todo.</i>
		 */
	public ElectricPowerSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger()) ;

		// Model implementation variable initialisation
		this.lastReading = -1.0 ;

		// Create the representation of the sensor bandwidth function
		this.readings = new Vector<PowerReading>() ;
	}
	
	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname =
			this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;

		// Initialise the look of the plotter
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries(SERIES) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.triggerReading = false ;

		this.lastReadingTime = initialTime.getSimulatedTime() ;
		this.readings.clear() ;
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		if (this.triggerReading) {
			if (this.plotter != null) {
				this.plotter.addData(
					SERIES,
					this.lastReadingTime,
					this.power.v) ;
				this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.power.v) ;
			}
			this.lastReading = this.power.v ;
			this.lastReadingTime =
					this.getCurrentStateTime().getSimulatedTime() ;

			Vector<EventI> ret = new Vector<EventI>(1) ;
			Time currentTime = 
					this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
			PowerReading wbr =
					new PowerReading(currentTime, this.power.v) ;
			ret.add(wbr) ;

			this.readings.addElement(wbr) ;
			this.logMessage(this.getCurrentStateTime() +
					"|output|power reading " +
					this.readings.size() + " with value = " +
					this.power.v) ;

			this.triggerReading = false ;
			return ret ;
		} else {
			return null ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.triggerReading) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		if (this.plotter != null) {
			this.plotter.addData(SERIES,
								 endTime.getSimulatedTime(),
								 this.lastReading) ;
		}

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return new ElectricPowerSensorReport(this.getURI(), this.readings) ;
	}

}
