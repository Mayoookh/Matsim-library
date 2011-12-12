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
package org.matsim.contrib.freight.vrp.basics;


import java.util.ArrayList;
import java.util.List;

import org.matsim.contrib.freight.vrp.algorithms.rr.api.TourAgent;
import org.matsim.contrib.freight.vrp.algorithms.rr.api.TourAgentFactory;
import org.matsim.contrib.freight.vrp.algorithms.rr.basics.Shipment;
import org.matsim.contrib.freight.vrp.algorithms.rr.basics.Solution;
import org.matsim.contrib.freight.vrp.api.Customer;
import org.matsim.contrib.freight.vrp.api.Node;
import org.matsim.contrib.freight.vrp.api.SingleDepotVRP;
import org.matsim.contrib.freight.vrp.api.VRP;


/**
 * 
 * @author stefan schroeder
 *
 */

public class VrpUtils {

	public static Tour createRoundTour(Customer depot, Customer n){
		Tour tour = createEmptyCustomerTour();
		tour.getActivities().add(createTourActivity(depot));
		tour.getActivities().add(createTourActivity(n));
		tour.getActivities().add(createTourActivity(depot));
		return tour;
	}
	
	public static Tour createRoundTour(Customer depot, Customer i, Customer j){
		Tour tour = createEmptyCustomerTour();
		assertCustomerRelation(i,j);
		tour.getActivities().add(createTourActivity(depot));
		tour.getActivities().add(createTourActivity(i));
		tour.getActivities().add(createTourActivity(j));
		tour.getActivities().add(createTourActivity(depot));
		return tour;
	}
	
	private static void assertCustomerRelation(Customer i, Customer j) {
		if(i.hasRelation()){
			if(i.getDemand() > 0 && j.getDemand() > 0){
				throw new IllegalStateException("cannot be a relation. both customers have goods to be picked up");
			}
			if(i.getDemand() < 0 && j.getDemand() < 0){
				throw new IllegalStateException("cannot be a relation. both customers have goods to be delivered");
			}
			if(i.getRelation().getCustomer().getId().equals(j.getId())){
				if(i.getDemand() > 0 && j.getDemand() < 0){
					return;
				}
				else{
					throw new IllegalStateException("invalid round-tour. customer i should be serviced before j");
				}
			}
		}
		
	}

	/**
	 * if customer.getDemand < 0, it is an delivery, i.e. freight is unloaded from the vehicle to the customer
	 * if customer.getDemand > 0, it is a pickup, i.e. freight is loaded from the costumer to the vehicle
	 * @param customer
	 * @return
	 */
	public static TourActivity createTourActivity(Customer customer) {
		TourActivity tourAct = null;
		double start = customer.getTheoreticalTimeWindow().getStart();
		double end = customer.getTheoreticalTimeWindow().getEnd();
		if(customer.hasRelation()){
			if(customer.getDemand() < 0){
				tourAct = new EnRouteDelivery(customer);
				tourAct.setTimeWindow(start, end);
			}
			else if(customer.getDemand() > 0){
				tourAct = new EnRoutePickup(customer);
				tourAct.setTimeWindow(start, end);
			}
			else {
				tourAct = new OtherDepotActivity(customer);
				tourAct.setTimeWindow(start, end);
			}
		}
		else{
			if(customer.getDemand() < 0){
				tourAct = new DeliveryFromDepot(customer);
				tourAct.setTimeWindow(start, end);
			}
			else if(customer.getDemand() > 0){
				tourAct = new PickupToDepot(customer);
				tourAct.setTimeWindow(start, end);
			}
			else {
				tourAct = new OtherDepotActivity(customer);
				tourAct.setTimeWindow(start, end);
			}
		}
		return tourAct;
	}
	
	public static Vehicle createVehicle(int newVehicleCapacity) {
		return new Vehicle(newVehicleCapacity);
	}
	
	public static Vehicle createVehicle(VehicleType type){
		return new Vehicle(type);
	}
	
	public static Tour createEmptyCustomerTour(){
		return new Tour();
	}
	
	public static Tour createEmptyRoundTour(Customer depot){
		Tour tour = createEmptyCustomerTour();
		tour.getActivities().add(createTourActivity(depot));
		tour.getActivities().add(createTourActivity(depot));
		return tour;
	}
	
	public static Solution copySolution(Solution solution, VRP vrp, TourAgentFactory tourAgentFactory){
		List<TourAgent> agents = new ArrayList<TourAgent>();
		for(TourAgent agent : solution.getTourAgents()){
			Tour tour = createEmptyCustomerTour();
			Vehicle vehicle = VrpUtils.createVehicle(agent.getVehicleCapacity());
			for(TourActivity tourAct : agent.getTourActivities()){
				TourActivity newCustomer = createTourActivity(tourAct.getCustomer());
				tour.getActivities().add(newCustomer);
			}
			TourAgent newTourAgent = tourAgentFactory.createTourAgent(tour, vehicle);
			agents.add(newTourAgent);
		}
		return new Solution(agents);  
	}
	
	public static TimeWindow createTimeWindow(double start, double end){
		return new TimeWindow(start, end);
	}
	
	public static Customer createCustomer(String id, Node node, int demand, double startTime, double endTime, double serviceTime){
		Customer customer = new CustomerImpl(id, node);
		customer.setDemand(demand);
		customer.setServiceTime(serviceTime);
		customer.setTheoreticalTimeWindow(startTime, endTime);
		return customer;
	}
	
	public static Shipment createShipment(Customer c1, Customer c2){
		return new Shipment(c1,c2);
	}
	
	public static String createId(String id){
		return id;
	}
	
	public static Node createNode(String id){
		return new NodeImpl(id);
	}

	public static Coordinate createCoord(int x, int y) {
		return new Coordinate(x,y);
	}
	
	public static Relation createRelation(Customer customer){
		return new Relation(customer);
	}
	
	public static Tour createRoundTour(SingleDepotVRP vrp, Customer from, Customer to){
		Tour tour = null;
		Customer depot = vrp.getDepot();
		if(vrp.getDepot().getId().equals(from.getId())){
			tour = VrpUtils.createRoundTour(depot, to);
		}
		else if(vrp.getDepot().getId().equals(to.getId())){
			tour = VrpUtils.createRoundTour(depot, from);
		}
		else {
			tour = VrpUtils.createRoundTour(depot, from, to);
		}
		return tour;
	}
	
	private static void assertNotNull(Vehicle vehicle) {
		if(vehicle == null){
			throw new IllegalStateException("vehicle is null. this cannot be");
		}
	}

	private static void assertNotNull(String depotId) {
		if(depotId == null){
			throw new IllegalStateException("id null. this cannot be.");
		}
	}
	
}
