package clean.equipments.tv.mil.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.TV.events.TVConsumptionEvent;

@ModelExternalEvents(imported = TicEvent.class,
exported = TVConsumptionEvent.class)
public class TVConsumptionMILModel 
extends AtomicHIOA
implements SGMILModelImplementationI{
	
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
		
		public TVConsumptionMILModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
			super(uri, simulatedTimeUnit, simulationEngine);
			consumptions = new Vector<TVConsumptionEvent>();
			this.updateConsumption = false;
			this.rgConsumption = new RandomDataGenerator();
			this.consumption = 0;
			
			
			this.setLogger(new StandardLogger()) ;
		}
		
		/**
		 * @see java.lang.Object#finalize()
		 */
		@Override
		protected void		finalize() throws Throwable
		{
			if (this.consPlotter != null) {
				this.consPlotter.dispose() ;
			}
			super.finalize();
		}
		
		// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------
		private static final long serialVersionUID = 1L;
		
		public static final String URI = TVConsumptionMILModel.class.getName();
		
		public static final String		COMPONENT_HOLDER_REF_PARAM_NAME =
				"tv consumption component reference" ;
		
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
		
		public static final String TVCONS_PLOTTING_PARAM_NAME = "tv-cons-plot";
		
		private static final String	SERIES = "TV consumption" ;
		
		/** Frame used to plot the consumption during the simulation.			*/
		protected XYPlotter			consPlotter ;
		
		private double consumption;
		
		/** reference on the object representing the component that holds the
		 *  model; enables the model to access the state of this component.		*/
		protected EmbeddingComponentAccessI componentRef ;
		
		// -------------------------------------------------------------------------
		// HIOA Model Variables
		// -------------------------------------------------------------------------
		
		@ImportedVariable (type = Double.class)
		protected Value<Double> tvBack;
		
		
		/**
		 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
		 */
		@Override
		public void			setSimulationRunParameters(
			Map<String, Object> simParams
			) throws Exception
		{
			// The reference to the embedding component
			this.componentRef =
				(EmbeddingComponentAccessI)
								simParams.get(COMPONENT_HOLDER_REF_PARAM_NAME) ;
		}
		
		/**
		 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
		 */
		@Override
		public void			initialiseState(Time initialTime)
		{
			this.rgConsumption.reSeedSecure();
			this.consumptions.clear();
			
			PlotterDescription pd =
					new PlotterDescription(
							"TV consumption",
							"Time (sec)",
							"Consumption (Watt)",
							100,
							0,
							600,
							400) ;
			this.consPlotter = new XYPlotter(pd) ;
			this.consPlotter.createSeries(SERIES) ;
			
			try {
				// set the debug level triggering the production of log messages.
				this.setDebugLevel(1) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
			
			// first data in the plotter to start the plot.
			this.consPlotter.addData(
					SERIES,
					initialTime.getSimulatedTime(),
					this.getCons());
			
			super.initialiseState(initialTime);
		}

		@Override
		public ArrayList<EventI> output() {		
			
			if(updateConsumption) {
				ArrayList<EventI> ret = new ArrayList<EventI>();
				this.logMessage("emitting new tv consumption level: " +
						this.getCons() + " watts.") ;
				
				Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
				TVConsumptionEvent e = new TVConsumptionEvent(t, consumption);
				try {
					ret.add(e);
				} catch (Exception exc) {
					throw new RuntimeException(exc) ;
				}
				consumptions.add(e);
				updateConsumption = false;
				return ret;
			}
			return null;
		}

		@Override
		public Duration timeAdvance() {
			if(this.componentRef == null) {
				if (this.updateConsumption) {
					// immediate internal event when a reading is triggered.
					return Duration.zero(this.getSimulatedTimeUnit()) ;
				} else {
					return Duration.INFINITY ;
				}
			}
			else {
				return new Duration(10.0, TimeUnit.SECONDS);
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
			boolean	ticReceived = false ;
			if (e instanceof TicEvent) {
				ticReceived = true ;
			}
			
			if (ticReceived) {
				this.consumption = generateConsumption();
				this.consPlotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.getCons());
				this.updateConsumption = true;
				
				
			}
		}
		
		/**
		 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
		 */
		@Override
		public void			endSimulation(Time endTime) throws Exception
		{
			this.consPlotter.addData(
					SERIES,
					endTime.getSimulatedTime(),
					this.getCons()) ;

			super.endSimulation(endTime) ;
		}
		
		/**
		 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
		 */
		@Override
		public SimulationReportI	getFinalReport() throws Exception
		{
			final String uri = this.uri ;
			return new SimulationReportI() {
						private static final long serialVersionUID = 1L;
						@Override
						public String getModelURI() {
							return uri ;
						}				
					};
					
			//return new TVConsumptionModelReport(this.getURI(),consumptions);


		}
		
		// -------------------------------------------------------------------------
		// Model-specific methods
		// -------------------------------------------------------------------------

		/**
		 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI#disposePlotters()
		 */
		@Override
		public void			disposePlotters() throws Exception
		{
			if (this.consPlotter != null) {
				this.consPlotter.dispose() ;
				this.consPlotter = null ;
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
		
		public double getCons() {
			return consumption;
		}
		
		
}
