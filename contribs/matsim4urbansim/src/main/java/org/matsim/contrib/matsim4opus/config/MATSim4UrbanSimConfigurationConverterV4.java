/* *********************************************************************** *
 * project: org.matsim.*
 * MATSimConfigObject.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * 
 */
package org.matsim.contrib.matsim4opus.config;

import org.apache.log4j.Logger;
import org.matsim.contrib.matsim4opus.matsim4urbansim.jaxbconfig2.ConfigType;
import org.matsim.contrib.matsim4opus.matsim4urbansim.jaxbconfig2.Matsim4UrbansimType;
import org.matsim.contrib.matsim4opus.matsim4urbansim.jaxbconfig2.MatsimConfigType;
import org.matsim.core.config.Config;
import org.matsim.core.config.Module;
import org.matsim.core.config.consistency.VspConfigConsistencyCheckerImpl;

/**
 * @author thomas
 * 
 * improvements dec'11:
 * - adjusting flow- and storage capacities to population sample rate. The
 * storage capacity includes a fetch factor to avoid backlogs and network breakdown
 * for small sample rates.
 * 
 * improvements jan'12:
 * - initGlobalSettings sets the number of available processors in the 
 * 	GlobalConfigGroup to speed up MATSim computations. Before that only
 * 	2 processors were used even if there are more.
 * 
 * improvements feb'12:
 * - setting mutationrange = 2h for TimeAllocationMutator (this shifts the departure times)
 * 
 * improvements march'12:
 * - extended the MATSim4UrbanSim configuration, e.g. a standard MATSim config can be loaded
 * 
 * improvements aug'12
 * - extended the MATSim4UrbanSim configuration: 
 *   - added a switch to select between between radius or shape-file distribution of locations within a zone
 *   - added a field "project_name" of the UrbanSim scenario as an identifier
 *   - added a time-of-a-day parameter
 *   - added beta parameter for mode bike
 *   
 * improvements/changes oct'12
 * - switched to qsim
 * 
 * changes dec'12
 * - switched matsim4urbansim config v2 parameters from v3 are out sourced into external matsim config
 * - introducing pseudo pt (configurable via external MATSim config)
 * - introducing new strategy module "changeSingeLegMode" (configurable via external MATSim config)
 * 
 * changes jan'13
 * - added attributes for pt stop, travel times and distances input files 
 *
 * changes march'13
 * - now, the external config overwrites all parameter settings that overlap with the settings 
 *   made in the MATSim4UrbanSim config (see also test cases in "ConfigLoadingTest")
 */
public class MATSim4UrbanSimConfigurationConverterV4 {
	// logger
	static final Logger log = Logger.getLogger(MATSim4UrbanSimConfigurationConverterV4.class);
	
	// MATSim config
	private Config config = null;

	// JAXB representation of matsim4urbansim config
	private final MatsimConfigType matsim4urbansimConfig ;

	/**
	 * constructor
	 * 
	 * @param config stores MATSim parameters
	 * @param matsim4urbansimConfig stores all parameters from matsim4urbansim config ( generated by UrbanSim )
	 */
	public MATSim4UrbanSimConfigurationConverterV4(final MatsimConfigType matsim4urbansimConfig){
		this.matsim4urbansimConfig = matsim4urbansimConfig;	
	}
	
	/**
	 * constructor
	 * 
	 * @param config stores MATSim parameters
	 * @param matsim4urbansimConfigFilename path to matsim config file
	 */
	public MATSim4UrbanSimConfigurationConverterV4(final String matsim4urbansimConfigFilename){
		this.matsim4urbansimConfig = M4UConfigUtils.unmarschal(matsim4urbansimConfigFilename); // loading and initializing MATSim config		
	}
	
	/**
	 * Transferring all parameter from matsim4urbansim config to internal MATSim config/scenario
	 * @return boolean true if initialization successful
	 */
	public boolean init(){
		
		try{
			// get root elements from JAXB matsim4urbansim config object
			ConfigType matsim4urbansimConfigPart1 = matsim4urbansimConfig.getConfig();
			Matsim4UrbansimType matsim4urbansimConfigPart2 = matsim4urbansimConfig.getMatsim4Urbansim();
			
			// loads the external MATSim config separately (to get additional MATSim4UrbanSim parameters)
			Module matsim4urbansimConfigPart3 = M4UConfigUtils.getM4UModuleFromExternalConfig(matsim4urbansimConfigPart1.getMatsimConfig().getInputFile());

			// creates an empty config to be filled by settings from the MATSim4UrbanSim and external config files
			this.config = M4UConfigUtils.createEmptyConfigWithSomeDefaults();

			M4UConfigUtils.initUrbanSimParameter(matsim4urbansimConfigPart2, matsim4urbansimConfigPart3, config);
			M4UConfigUtils.initMATSim4UrbanSimControler(matsim4urbansimConfigPart2, matsim4urbansimConfigPart3, config);
			M4UConfigUtils.initAccessibilityParameters(matsim4urbansimConfigPart2, matsim4urbansimConfigPart3, config);
			
//<<<<<<< HEAD
//			MATSim4UrbanSimConfigUtils.initNetwork(matsim4urbansimConfigPart1, config);
//			MATSim4UrbanSimConfigUtils.initInputPlansFile(matsim4urbansimConfigPart1, config);
//			MATSim4UrbanSimConfigUtils.initControler(matsim4urbansimConfigPart1, config);
//			MATSim4UrbanSimConfigUtils.initPlanCalcScore(matsim4urbansimConfigPart1, config);
//			MATSim4UrbanSimConfigUtils.initStrategy(matsim4urbansimConfigPart1, config);
//			
//			/*
//			 *  why is it necessary to set some other defaults than the defaults from MATSim?
//			 *  With the newest TripRouterFactory (r24248) this will lead to to a runtime-error
//			 *  when for any of the intialized modes a different behavior (e.g. freespeedfactor 
//			 *  instead of freespeed) is set. Daniel, May '13
//			 */
////			MATSim4UrbanSimConfigUtils.initPlanCalcRoute(config);
//			MATSim4UrbanSimConfigUtils.initQSim(matsim4urbansimConfig, config);
//=======
			M4UConfigUtils.initNetwork(matsim4urbansimConfigPart1, config);
			M4UConfigUtils.initInputPlansFile(matsim4urbansimConfigPart1, config);
			M4UConfigUtils.initControler(matsim4urbansimConfigPart1, config);
			M4UConfigUtils.initPlanCalcScore(matsim4urbansimConfigPart1, config);
			M4UConfigUtils.initStrategy(matsim4urbansimConfigPart1, config);

			M4UConfigUtils.initPlanCalcRoute(config);
			M4UConfigUtils.initQSim(matsim4urbansimConfig, config);
//>>>>>>> bringing accessibility config closer to matsim standards
			
			// loading the external MATSim config in to the initialized config
			// overlapping parameter settings (in MATSim4UrbanSim and external MATSim config)
			// are overwritten by the external MATSim settings
			M4UConfigUtils.loadExternalConfigAndOverwriteMATSim4UrbanSimSettings(matsim4urbansimConfigPart1, config);
			
//			// show final settings
			// (these are not visible in the matsim config dump :-( :-( and thus need to be done separately. kai, apr'13)
			M4UConfigUtils.printUrbanSimParameterSettings( M4UConfigUtils.getUrbanSimParameterConfigAndPossiblyConvert(config) );
			M4UConfigUtils.printMATSim4UrbanSimControlerSettings( M4UConfigUtils.getMATSim4UrbaSimControlerConfigAndPossiblyConvert(config) );
//			M4UAccessibilityConfigUtils.printAccessibilityParameterSettings( M4UAccessibilityConfigUtils.getAccessibilityParameterConfigPossiblyEmpty(config) );
			
			config.addConfigConsistencyChecker( new VspConfigConsistencyCheckerImpl() ) ;
			config.addConfigConsistencyChecker( new M4UConfigConsistencyChecker() ) ;
			
			M4UConfigUtils.checkConfigConsistencyAndWriteToLog(config, "at the end of the matsim4urbansim config converter") ;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Config getConfig(){
			return this.config;
	}
	

	public static void main(String[] args) {
	
//		MatsimConfigType mct = new MatsimConfigType();
//		
//		MATSim4UrbanSimConfigurationConverterV4 m = new MATSim4UrbanSimConfigurationConverterV4(mct);
//		m.stopMATSimForMixedAccessibilities(0, 0, 0, 0, 0, 0, 0, 0, 0);
//		m.stopMATSimForMixedAccessibilities(1, 1, 1, 0, 0, 0, 0, 0, 0);
//		m.stopMATSimForMixedAccessibilities(0, 0, 0, 1, 1, 0, 0, 0, 0);
//		m.stopMATSimForMixedAccessibilities(0, 0, 0, 0, 0, 0, 0, 1, 0);
//		m.stopMATSimForMixedAccessibilities(0, 1, 0, 0, 0, 0, 0, 1, 0);
		// testing calculation of storage capacity fetch factor
		for(double sample = 0.01; sample <=1.; sample += 0.01){
			
			double factor = Math.pow(sample, -0.25); // same as: 1. / Math.sqrt(Math.sqrt(sample))
			double storageCap = sample * factor;
			
			System.out.println("Sample rate " + sample + " leads to a fetch fector of: " + factor + " and a StroraceCapacity of: " + storageCap );
		}
		
		for(int i = 0; i <= 100; i++){
			System.out.println("i = " + i + " disable int = " + (int) Math.ceil(i * 0.8)+ " disable double = " + i * 0.8);			
		}
	}
}

