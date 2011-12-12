/*******************************************************************************
 * Copyright (C) 2011 Stefan Schroeder.
 * eMail: stefan.schroeder@kit.edu
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.matsim.contrib.freight.vrp.algorithms.rr;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.matsim.contrib.freight.vrp.algorithms.rr.api.RuinAndRecreateListener;
import org.matsim.contrib.freight.vrp.algorithms.rr.api.TourAgent;
import org.matsim.contrib.freight.vrp.algorithms.rr.api.TourAgentFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.basics.RRTourAgentFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.basics.Solution;
import org.matsim.contrib.freight.vrp.algorithms.rr.recreation.BestInsertion;
import org.matsim.contrib.freight.vrp.algorithms.rr.recreation.RecreationListener;
import org.matsim.contrib.freight.vrp.algorithms.rr.ruin.RadialRuin;
import org.matsim.contrib.freight.vrp.algorithms.rr.ruin.RandomRuin;
import org.matsim.contrib.freight.vrp.algorithms.rr.thresholdFunctions.SchrimpfsRRThresholdFunction;
import org.matsim.contrib.freight.vrp.api.SingleDepotVRP;
import org.matsim.contrib.freight.vrp.api.VRP;
import org.matsim.contrib.freight.vrp.basics.SingleDepotInitialSolutionFactoryImpl;
import org.matsim.contrib.freight.vrp.basics.Tour;
import org.matsim.contrib.freight.vrp.basics.Vehicle;
import org.matsim.contrib.freight.vrp.basics.VrpUtils;


/**
 * Creates ready to use ruin-and-recreate-algorithms.
 * 
 * @author stefan schroeder
 *
 */


public class StandardRuinAndRecreateFactory implements RuinAndRecreateFactory {
	
	private static Logger logger = Logger.getLogger(StandardRuinAndRecreateFactory.class);
	
	private Collection<RecreationListener> recreationListeners = new ArrayList<RecreationListener>();
	
	private Collection<RuinAndRecreateListener> ruinAndRecreationListeners = new ArrayList<RuinAndRecreateListener>();

	private int warmUp = 10;
	
	private int iterations = 100;
	
	public StandardRuinAndRecreateFactory(int warmUp, int iterations) {
		super();
		this.warmUp = warmUp;
		this.iterations = iterations;
	}

	public StandardRuinAndRecreateFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see vrp.algorithms.ruinAndRecreate.factories.RuinAndRecreateFactory#createAlgorithm(vrp.api.SingleDepotVRP, java.util.Collection, int)
	 */
	@Override
	public RuinAndRecreate createAlgorithm(SingleDepotVRP vrp, Collection<Tour> tours, int vehicleCapacity){
		RRTourAgentFactory tourAgentFactory = new RRTourAgentFactory(vrp);
		Solution initialSolution = getInitialSolution(vrp,tours,tourAgentFactory,vehicleCapacity);
		RuinAndRecreate ruinAndRecreateAlgo = new RuinAndRecreate(vrp, initialSolution, iterations);
		ruinAndRecreateAlgo.setWarmUpIterations(warmUp);
		ruinAndRecreateAlgo.setTourAgentFactory(tourAgentFactory);
		ruinAndRecreateAlgo.setRuinStrategyManager(new RuinStrategyManager());
		
		BestInsertion recreationStrategy = new BestInsertion(vrp);
		recreationStrategy.setTourAgentFactory(tourAgentFactory);
		ruinAndRecreateAlgo.setRecreationStrategy(recreationStrategy);
		
		RadialRuin radialRuin = new RadialRuin(vrp);
		radialRuin.setFractionOfAllNodes(0.2);
		
		RandomRuin randomRuin = new RandomRuin(vrp);
		randomRuin.setFractionOfAllNodes2beRuined(0.3);
		
		ruinAndRecreateAlgo.getRuinStrategyManager().addStrategy(radialRuin, 0.5);
		ruinAndRecreateAlgo.getRuinStrategyManager().addStrategy(randomRuin, 0.5);
		ruinAndRecreateAlgo.setThresholdFunction(new SchrimpfsRRThresholdFunction(0.1));
		
		for(RuinAndRecreateListener l : ruinAndRecreationListeners){
			ruinAndRecreateAlgo.getListeners().add(l);
		}
		
		return ruinAndRecreateAlgo;
	}
	

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	private Solution getInitialSolution(VRP vrp, Collection<Tour> tours, TourAgentFactory tourAgentFactory, int vehicleCapacity) {
		Collection<TourAgent> tourAgents = new ArrayList<TourAgent>();
		for(Tour tour : tours){
			Vehicle vehicle = VrpUtils.createVehicle(vehicleCapacity);
			tourAgents.add(tourAgentFactory.createTourAgent(tour, vehicle));
		}
		return new Solution(tourAgents);
	}

	public void setWarmUp(int nOfWarmUpIterations) {
		this.warmUp = nOfWarmUpIterations;
		
	}


	public void addRuinAndRecreateListener(RuinAndRecreateListener l){
		ruinAndRecreationListeners.add(l);
	}

}
