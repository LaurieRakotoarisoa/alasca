package simulation.TV.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import simulation.Controller.TVController;
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.TV.events.TVSwitch;

public class TVMILCoupledModel 
extends CoupledModel
implements SGMILModelImplementationI{

	public TVMILCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String	URI = TVMILCoupledModel.class.getCanonicalName() ;
	
	public static class	TVModelReport
	extends		AbstractSimulationReport
	{
		private static final long		serialVersionUID = 1L ;

		public			TVModelReport(
			String modelURI,
			SimulationReportI[] reports
			)
		{
			super(modelURI) ;
			assert	reports.length == 4 ;

		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "-----------------------------------------\n" ;
			ret += "TV Model report\n" ;
			return ret ;
		}
	}
		
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		SimulationReportI[] reports =
							new SimulationReportI[this.submodels.length] ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			reports[i] = this.submodels[i].getFinalReport() ;
		}
		return new TVModelReport(this.getURI(), reports) ;
	}
	
	/**
	 * build the simulation architecture corresponding to this coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the simulation architecture corresponding to this coupled model.
	 * @throws Exception	<i>TO DO.</i>
	 */
	public static Architecture	build() throws Exception
	{
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>() ;
		
		atomicModelDescriptors.put(
				TicModel.URI,
				AtomicModelDescriptor.create(
						TicModel.class,
						TicModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				TVStateModel.URI,
				AtomicHIOA_Descriptor.create(
						TVStateModel.class,
						TVStateModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				TVConsumption.URI,
				AtomicHIOA_Descriptor.create(
						TVConsumption.class,
						TVConsumption.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				TVUserModel.URI,
				AtomicModelDescriptor.create(
						TVUserModel.class,
						TVUserModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				TVController.URI,
				AtomicModelDescriptor.create(
						TVController.class,
						TVController.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<String,CoupledModelDescriptor>() ;

		Set<String> submodels = new HashSet<String>() ;
		submodels.add(TVStateModel.URI) ;
		submodels.add(TVConsumption.URI) ;
		submodels.add(TVUserModel.URI);
		submodels.add(TVController.URI);
		submodels.add(TicModel.URI);
		
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>() ;
				
		EventSource from1 =
		new EventSource(TicModel.URI, TicEvent.class) ;
		EventSink[] to1 =
		new EventSink[] {
			new EventSink(TVConsumption.URI, TicEvent.class)} ;
		connections.put(from1, to1) ;
		
		EventSource from2 =
		new EventSource(TVUserModel.URI, TVSwitch.class) ;
		EventSink[] to2 = new EventSink[] {
		new EventSink(TVStateModel.URI, TVSwitch.class)} ;
		connections.put(from2, to2) ;
		
		EventSource from3 =
		new EventSource(TVController.URI, EconomyEvent.class) ;
		EventSink[] to3 = new EventSink[] {
		new EventSink(TVStateModel.URI, EconomyEvent.class)} ;
		connections.put(from3, to3) ;
		
		EventSource from4 =
				new EventSource(TVController.URI, NoEconomyEvent.class) ;
				EventSink[] to4 = new EventSink[] {
				new EventSink(TVStateModel.URI, NoEconomyEvent.class)} ;
				connections.put(from4, to4) ;
				
		Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource, VariableSink[]>();
		
		VariableSource source1 =
				new VariableSource("tvBack",
								   Double.class,
								   TVStateModel.URI) ;
		VariableSink[] sinks1 =
			new VariableSink[] {
					new VariableSink("tvBack",
									 Double.class,
									 TVConsumption.URI)} ;
		bindings.put(source1, sinks1);
			
				
		coupledModelDescriptors.put(
				TVMILCoupledModel.URI,
				new CoupledHIOA_Descriptor(
						TVMILCoupledModel.class,
						TVMILCoupledModel.URI,
						submodels,
						null,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						null,
						null,
						bindings)) ;
		
		return new Architecture(
				TVMILCoupledModel.URI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				TimeUnit.SECONDS);
			
	
	}
	
	
	
	
	

}
