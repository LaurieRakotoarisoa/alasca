package clean.equipments.oven.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import clean.environment.UserMILModel;
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
import simulation.Controller.events.EconomyEvent;
import simulation.Controller.events.NoEconomyEvent;
import simulation.oven.events.OvenSwitchEvent;

public class OvenMILCoupledModel 
extends CoupledModel
implements SGMILModelImplementationI{

	public OvenMILCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String	URI = OvenMILCoupledModel.class.getName() ;
	
	public static class	OvenModelReport
	extends		AbstractSimulationReport
	{
		private static final long		serialVersionUID = 1L ;

		public			OvenModelReport(
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
			ret += "Oven Model report\n" ;
			return ret ;
		}
	}
	
	/**
	 * create the atomic model descriptors for all atomic models having this
	 * coupled model as ancestor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the atomic model descriptors of this model descendants.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Map<String,AbstractAtomicModelDescriptor>
												createAtomicModelDescriptors()
	throws Exception
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
				OvenStateMILModel.URI,
				AtomicHIOA_Descriptor.create(
						OvenStateMILModel.class,
						OvenStateMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
				OvenConsumptionMILModel.URI,
				AtomicHIOA_Descriptor.create(
						OvenConsumptionMILModel.class,
						OvenConsumptionMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		
		atomicModelDescriptors.put(
			UserMILModel.URI,
				AtomicModelDescriptor.create(
						UserMILModel.class,
						UserMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE));

		return atomicModelDescriptors ;
	}
	
	/**
	 * create the coupled model descriptors for all coupled models having this
	 * coupled model as ancestor and also including this coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the coupled model descriptors of this model and its descendants.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Map<String,CoupledModelDescriptor>
												createCoupledModelDescriptors()
	throws Exception
	{
		
		Map<Class<? extends EventI>,EventSink[]> imported =
				new HashMap<Class<? extends EventI>,EventSink[]>() ;
		
		imported.put(EconomyEvent.class,
				new EventSink[] {
						new EventSink(OvenStateMILModel.URI,
								EconomyEvent.class)});
		imported.put(NoEconomyEvent.class,
				new EventSink[] {
						new EventSink(OvenStateMILModel.URI,
								NoEconomyEvent.class)});
		
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
								new HashMap<String,CoupledModelDescriptor>() ;
		
		Set<String> submodels = new HashSet<String>() ;
		submodels.add(OvenStateMILModel.URI) ;
		submodels.add(OvenConsumptionMILModel.URI) ;
		submodels.add(TicModel.URI);
		submodels.add(UserMILModel.URI);
		
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>() ;
				
		EventSource from1 =
		new EventSource(TicModel.URI, TicEvent.class) ;
		EventSink[] to1 =
		new EventSink[] {
			new EventSink(OvenConsumptionMILModel.URI, TicEvent.class)} ;
		connections.put(from1, to1) ;
		
		EventSource from2 =
				new EventSource(UserMILModel.URI, OvenSwitchEvent.class) ;
				EventSink[] to2 =
				new EventSink[] {
					new EventSink(OvenStateMILModel.URI, OvenSwitchEvent.class)} ;
				connections.put(from2, to2) ;
				
		Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource, VariableSink[]>();
		
		VariableSource source1 =
				new VariableSource("temperature",
								   Double.class,
								   OvenStateMILModel.URI) ;
		VariableSink[] sinks1 =
			new VariableSink[] {
					new VariableSink("temperature",
									 Double.class,
									 OvenConsumptionMILModel.URI)} ;
		bindings.put(source1, sinks1);
			
				
		coupledModelDescriptors.put(
				OvenMILCoupledModel.URI,
				new CoupledHIOA_Descriptor(
						OvenMILCoupledModel.class,
						OvenMILCoupledModel.URI,
						submodels,
						imported,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						null,
						null,
						bindings)) ;
		
		return coupledModelDescriptors;
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
		return new OvenModelReport(this.getURI(), reports) ;
	}
	
	/**
	 * @see fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI#disposePlotters()
	 */
	@Override
	public void			disposePlotters() throws Exception
	{
		for (int i = 0 ; i < this.submodels.length ; i++) {
			ModelDescriptionI m =
					this.submodels[i].getDescendentModel(
												this.submodels[i].getURI()) ;
			if(m instanceof SGMILModelImplementationI) { 
				((SGMILModelImplementationI)m).disposePlotters() ;
			}
		}
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
	public static Architecture	buildArchitecture() throws Exception
	{		
		
		return new Architecture(
				OvenMILCoupledModel.URI,
				createAtomicModelDescriptors(),
				createCoupledModelDescriptors(),
				TimeUnit.SECONDS);
			
	
	}
	
	
	
	
	

}
