package fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an example
// for the extension of the BCM component model that aims to define a components
// tailored for cyber-physical control systems (CPCS) for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetHigh;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetLow;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOff;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOn;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
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
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;
import java.util.HashMap;
import java.util.HashSet;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerCoupledModel</code> implements the DEVS MIL
 * simulation coupled model for the hair dryer example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HairDryerMILCoupledModel
extends		CoupledModel
implements	SGMILModelImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** URI of the unique instance of this class (in this example).			*/
	public static final String	URI = HairDryerMILCoupledModel.class.getName() ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				HairDryerMILCoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine,
		ModelDescriptionI[] submodels,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
			  imported, reexported, connections,
			  importedVars, reexportedVars, bindings);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		StandardCoupledModelReport ret =
							new StandardCoupledModelReport(this.getURI()) ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			ret.addReport(this.submodels[i].getFinalReport()) ;
		}
		return ret ;
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
			assert	m instanceof SGMILModelImplementationI ;
			((SGMILModelImplementationI)m).disposePlotters() ;
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
				HairDryerMILModel.URI,
				AtomicHIOA_Descriptor.create(
						HairDryerMILModel.class,
						HairDryerMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
		atomicModelDescriptors.put(
				HairDryerMILUserModel.URI,
				AtomicModelDescriptor.create(
						HairDryerMILUserModel.class,
						HairDryerMILUserModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

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
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
								new HashMap<String,CoupledModelDescriptor>() ;


		Set<String> submodels = new HashSet<String>() ;
		submodels.add(HairDryerMILModel.URI) ;
		submodels.add(HairDryerMILUserModel.URI) ;

		Map<Class<? extends EventI>,EventSink[]> imported =
				new HashMap<Class<? extends EventI>,EventSink[]>() ;
		imported.put(
				SwitchOn.class, 
				new EventSink[] {
						new EventSink(HairDryerMILModel.URI, SwitchOn.class)
				}) ;
		imported.put(
				SwitchOff.class, 
				new EventSink[] {
						new EventSink(HairDryerMILModel.URI, SwitchOff.class)
				}) ;
		imported.put(
				SetHigh.class, 
				new EventSink[] {
						new EventSink(HairDryerMILModel.URI, SetHigh.class)
				}) ;
		imported.put(
				SetLow.class, 
				new EventSink[] {
						new EventSink(HairDryerMILModel.URI, SetLow.class)
				}) ;

		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
		reexported.put(
				ConsumptionIntensity.class,
				new ReexportedEvent(HairDryerMILModel.URI,
									ConsumptionIntensity.class)) ;

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>() ;
		EventSource from1 =
				new EventSource(HairDryerMILUserModel.URI, SwitchOn.class) ;
		EventSink[] to1 =
				new EventSink[] {
						new EventSink(HairDryerMILModel.URI, SwitchOn.class)} ;
		connections.put(from1, to1) ;
		EventSource from2 =
				new EventSource(HairDryerMILUserModel.URI, SwitchOff.class) ;
		EventSink[] to2 = new EventSink[] {
				new EventSink(HairDryerMILModel.URI, SwitchOff.class)} ;
		connections.put(from2, to2) ;
		EventSource from3 =
				new EventSource(HairDryerMILUserModel.URI, SetLow.class) ;
		EventSink[] to3 = new EventSink[] {
				new EventSink(HairDryerMILModel.URI, SetLow.class)} ;
		connections.put(from3, to3) ;
		EventSource from4 =
				new EventSource(HairDryerMILUserModel.URI, SetHigh.class) ;
		EventSink[] to4 = new EventSink[] {
				new EventSink(HairDryerMILModel.URI, SetHigh.class)} ;
		connections.put(from4, to4) ;

		coupledModelDescriptors.put(
				HairDryerMILCoupledModel.URI,
				new CoupledHIOA_Descriptor(
						HairDryerMILCoupledModel.class,
						HairDryerMILCoupledModel.URI,
						submodels,
						imported,
						reexported,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						null,
						null,
						null)) ;

		return coupledModelDescriptors ;
	}

	/**
	 * build the simulation architecture corresponding to this coupled model
	 * as a stand alone simulator.
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
						HairDryerMILCoupledModel.URI,
						createAtomicModelDescriptors(),
						createCoupledModelDescriptors(),
						TimeUnit.SECONDS);
	}
}
// -----------------------------------------------------------------------------
