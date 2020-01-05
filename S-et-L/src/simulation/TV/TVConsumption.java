package simulation.TV;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
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
		this.updateConsumption = false;
		this.rgConsumption = new RandomDataGenerator();
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "TV-CONSUMPTION";
	
	/** stored output events for report */
	protected Vector<TVConsumptionEvent> consumptions;
	
	/** true when tv consumption must be updated */
	protected boolean updateConsumption;
	
	/** random generator for consumption depending on rate backlight parameter */
	protected final RandomDataGenerator rgConsumption;
	
	/** minimum factor to generate consumption depending on backlight */
	protected final double MIN_RATE_BL = 2.5;
	
	/** maximum factor to generate consumption depending on backlight */
	protected final double MAX_RATE_BL = 3.0;
	
	// -------------------------------------------------------------------------
	// HIOA Model Variables
	// -------------------------------------------------------------------------
	
	@ImportedVariable (type = Double.class)
	protected Value<Double> tvBack;
	

	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = new Vector<EventI>();
		if(updateConsumption) {
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
			TVConsumptionEvent e = new TVConsumptionEvent(t, generateConsumption());
			ret.add(e);
			consumptions.add(e);
			updateConsumption = false;
		}
		return ret;
	}

	@Override
	public Duration timeAdvance() {
		return Duration.one(this.getSimulatedTimeUnit());
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.rgConsumption.reSeed();
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
	 * generate TV consumption as double value depending on tv backlight
	 * @return TV consumption as double value
	 */
	public double generateConsumption() {
		double rateConsumption = rgConsumption.nextUniform(MIN_RATE_BL, MAX_RATE_BL);
		assert rateConsumption > MIN_RATE_BL && rateConsumption < MAX_RATE_BL;
		double newConsumption = this.tvBack.v * rateConsumption; 
		return newConsumption;
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
