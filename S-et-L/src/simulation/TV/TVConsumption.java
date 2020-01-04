package simulation.TV;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import simulation.TV.events.TVConsumptionEvent;

@ModelExternalEvents(imported = TicEvent.class,
					exported = TVConsumptionEvent.class)
public class TVConsumption 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------
	
	public static class TVConsumptionModelReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<TVConsumptionEvent>	readings ;

		public			TVConsumptionModelReport(
			String modelURI,
			Vector<TVConsumptionEvent> readings
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
			ret += "TV Consumption Model Report\n" ;
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
	
	public TVConsumption(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		consumptions = new Vector<TVConsumptionEvent>();
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "TV-CONSUMPTION";
	
	/** stored output events for report */
	protected Vector<TVConsumptionEvent> consumptions;
	
	/**Energy consumption when TV is turned on */
	public static final double CONS_AT_IGNITION = 90.0;

	@ImportedVariable (type = Double.class)
	protected Value<Double> tv_backlight;
	

	@Override
	public Vector<EventI> output() {
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return TicModel.STANDARD_DURATION;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new TVConsumptionModelReport(this.getURI(),consumptions);
	}

}
