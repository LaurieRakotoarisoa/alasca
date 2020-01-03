package simulation.TV;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;

public class TVModel 
extends CoupledModel{

	public TVModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
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
	
	public static final String	URI = "TV-MODEL" ;
	
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
			assert	reports.length == 2 ;

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

}
