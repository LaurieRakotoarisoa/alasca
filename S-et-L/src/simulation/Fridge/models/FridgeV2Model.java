package simulation.Fridge.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
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
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.Fridge.events.CloseDoor;
import simulation.Fridge.events.OpenDoor;
import utils.fridge.FridgeMode;

@ModelExternalEvents( imported = {CloseDoor.class,
								  OpenDoor.class,
								  NoEconomyEvent.class,
								  EconomyEvent.class,
								  TicEvent.class
									})
public class FridgeV2Model 
extends AtomicHIOA{
	
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>StatePiece</code> describe the state of the fridge at specific time.<p>
	 * It is used to report the evolution of the fridge door state at the end of a simulation
	 * @author Laurie Rakotoarisoa
	 *
	 */
	protected class StatePiece{
		FridgeMode state;
		Time time;
		
		protected StatePiece(FridgeMode state, Time time){
			this.state = state;
			this.time = time;
		}
		
		@Override
		public String toString() {
			return "("+state+" at "+time+")";
		}
	}
	
	public static class FridgeStateReport 
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<StatePiece>	readings ;

		public			FridgeStateReport(
			String modelURI,
			Vector<StatePiece> readings
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
			ret += "Fridge State V2 Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of states = " + this.readings.size() + "\n" ;
			ret += "States :\n" ;
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i) + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}
	
	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------
	public FridgeV2Model(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.setLogger(new StandardLogger()) ;
		this.setDebugLevel(1);
		this.states = new Vector<FridgeV2Model.StatePiece>();
		this.rgConsumption = new RandomDataGenerator();
		assert this.fridgeConsumption != null;
		this.staticInitialiseVariables();
	}

	// -------------------------------------------------------------------------
	// Constants and Variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	private FridgeMode currentState;
	
	private Vector<StatePiece> states;
	
	public static final String URI = "fridge-v2"; 
	
	/** default mean consumption when targeting high power */
	public static double MEAN_POWER_CONS = 100.0;
		
	/** mean consumption when targeting high power */
	public static double HIGH_POWER_CONS = 300.0;
	
	/** mean consumption when targeting low power */
	public static double LOW_POWER_CONS = 50.0;
	
	/** difference power to generate a random consumption */
	public static double SIGMA = 10.0;
	
	/** random generator for consumption  */
	protected final RandomDataGenerator rgConsumption;
	
	public static final String FRIDGECONS_PLOTTING_PARAM_NAME = "fridge-cons-plot";

	private static final String	SERIES = "Fridge consumption" ;
	
	/** Frame used to plot the consumption during the simulation.			*/
	protected XYPlotter			consPlotter ;
	
	// -------------------------------------------------------------------------
	// HIOA Model Variables
	// -------------------------------------------------------------------------
	@ExportedVariable (type = Double.class)
	protected final Value<Double> fridgeConsumption =
									new Value<Double>(this, MEAN_POWER_CONS);
	
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
				FRIDGECONS_PLOTTING_PARAM_NAME ;
	PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
	this.consPlotter = new XYPlotter(pd) ;
	this.consPlotter.createSeries(SERIES) ;
	
	}
	
	@Override
	public Vector<EventI> output() {
		return null;
	}

	@Override
	public Duration timeAdvance() {
		return Duration.INFINITY;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.rgConsumption.reSeedSecure();
		this.currentState = FridgeMode.On_Close;
		if (this.consPlotter != null) {
			this.consPlotter.initialise() ;
			this.consPlotter.showPlotter() ;
		}
		super.initialiseState(initialTime);
		states.add(new StatePiece(currentState, initialTime));
		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		
		//update current consumption
		this.updateCons(this.getCurrentStateTime());
		// Plotting
		if (this.consPlotter != null) {
			this.consPlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.fridgeConsumption.v) ;
		}
		super.userDefinedInternalTransition(elapsedTime) ;
		
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
		if(e instanceof OpenDoor) {
			openDoor(this.getCurrentStateTime());
			
		}
		else if(e instanceof CloseDoor) {
			closeDoor(this.getCurrentStateTime());
		}
		else if(e instanceof EconomyEvent) {
			setEconomyMode(true, this.getCurrentStateTime());
		}
		else if(e instanceof NoEconomyEvent) {
			setEconomyMode(false, this.getCurrentStateTime());
		}
		
	}
	
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new FridgeStateReport(getURI(), states);
	}
	
	public void openDoor(Time current) {
		assert this.currentState != FridgeMode.Off_Open && this.currentState != FridgeMode.On_Open;
		if(this.currentState == FridgeMode.On_Close) {
			this.currentState = FridgeMode.On_Open;
		}
		else {
			this.currentState = FridgeMode.Off_Open;
		}
		states.add(new StatePiece(currentState, current));
	}
	
	public void closeDoor(Time current) {
		assert this.currentState != FridgeMode.Off_Close && this.currentState != FridgeMode.On_Close;
		if(this.currentState == FridgeMode.On_Open) {
			this.currentState = FridgeMode.On_Close;
		}
		else {
			this.currentState = FridgeMode.Off_Close;
		}
		states.add(new StatePiece(currentState, current));
	}
	
	private void setEconomyMode(boolean b, Time current) {
		if(b) {
			if(this.currentState == FridgeMode.On_Close) {
				this.currentState = FridgeMode.Off_Close;
			}
			else if(this.currentState == FridgeMode.On_Open) {
				this.currentState = FridgeMode.Off_Open;
			}
		}
		else {
			if(this.currentState == FridgeMode.Off_Close) {
				this.currentState = FridgeMode.On_Close;
			}
			else if(this.currentState == FridgeMode.Off_Open) {
				this.currentState = FridgeMode.On_Open;
			}
		}
		states.add(new StatePiece(currentState, current));
	}
	
	public void updateCons(Time current) {
		if(this.currentState == FridgeMode.On_Close || this.currentState == FridgeMode.Off_Open) {
			this.fridgeConsumption.v = rgConsumption.nextUniform(MEAN_POWER_CONS-SIGMA,MEAN_POWER_CONS+SIGMA);
		}
		
		else if(this.currentState == FridgeMode.On_Open) {
			this.fridgeConsumption.v = rgConsumption.nextUniform(HIGH_POWER_CONS-SIGMA,HIGH_POWER_CONS+SIGMA);
		}
		else if(this.currentState == FridgeMode.Off_Close) {
			this.fridgeConsumption.v = rgConsumption.nextUniform(LOW_POWER_CONS-SIGMA,LOW_POWER_CONS+SIGMA);
		}
		this.fridgeConsumption.time = current;
			
	}
	
	

}
