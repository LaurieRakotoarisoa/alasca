package fr.sorbonne_u.components.cyphy.examples.hem.simulations;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.HairDryerMILCoupledModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.SGMILModelImplementationI;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetHigh;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SetLow;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOff;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.hairdryer.mil.models.events.SwitchOn;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.manager.mil.models.EnergyManagerMILModel;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.events.ConsumptionIntensity;
import fr.sorbonne_u.components.cyphy.examples.hem.equipments.meter.mil.models.ElectricMeterMILModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunMILSimulation</code> runs a MIL simulation for all models
 * in the household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2020-01-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunMILSimulation
{
	public static void	main(String[] args)
	{
		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>() ;

			atomicModelDescriptors.put(
				ElectricMeterMILModel.URI,
				AtomicModelDescriptor.create(
						ElectricMeterMILModel.class,
						ElectricMeterMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			atomicModelDescriptors.put(
				EnergyManagerMILModel.URI,
				AtomicModelDescriptor.create(
						EnergyManagerMILModel.class,
						EnergyManagerMILModel.URI,
						TimeUnit.SECONDS,
						null,
						SimulationEngineCreationMode.ATOMIC_ENGINE)) ;
			atomicModelDescriptors.putAll(
				HairDryerMILCoupledModel.createAtomicModelDescriptors()) ;

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>() ;

			coupledModelDescriptors.putAll(
				HairDryerMILCoupledModel.createCoupledModelDescriptors()) ;

			Set<String> submodels = new HashSet<String>() ;
			submodels.add(ElectricMeterMILModel.URI) ;
			submodels.add(EnergyManagerMILModel.URI) ;
			submodels.add(HairDryerMILCoupledModel.URI) ;

			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>() ;
			connections.put(
				new EventSource(HairDryerMILCoupledModel.URI,
								ConsumptionIntensity.class),
				new EventSink[] {
						new EventSink(ElectricMeterMILModel.URI,
									  ConsumptionIntensity.class)
				}) ;
			connections.put(
				new EventSource(ElectricMeterMILModel.URI,
								ConsumptionIntensity.class),
				new EventSink[] {
						new EventSink(EnergyManagerMILModel.URI,
									  ConsumptionIntensity.class)
				}) ;
			connections.put(
				new EventSource(EnergyManagerMILModel.URI, SwitchOn.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
									  SwitchOn.class)	
				}) ;
			connections.put(
				new EventSource(EnergyManagerMILModel.URI, SwitchOff.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
									  SwitchOff.class)	
				}) ;
			connections.put(
				new EventSource(EnergyManagerMILModel.URI, SetHigh.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
									  SetHigh.class)	
				}) ;
			connections.put(
				new EventSource(EnergyManagerMILModel.URI, SetLow.class),
				new EventSink[] {
						new EventSink(HairDryerMILCoupledModel.URI,
									  SetLow.class)	
				}) ;

			coupledModelDescriptors.put(
				SGCoupledModel.URI,
				new CoupledModelDescriptor(
						SGCoupledModel.class,
						SGCoupledModel.URI,
						submodels,
						null,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE)) ;

			Architecture arch =
				new Architecture(
						SGCoupledModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS) ;

			SimulationEngine se = arch.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 500.0) ;
			Thread.sleep(5000L) ;
			SGMILModelImplementationI m =
					(SGMILModelImplementationI)
										se.getDescendentModel(se.getURI()) ;
			m.disposePlotters() ;
			System.out.println("end.") ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
