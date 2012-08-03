/* *********************************************************************** *
 * project: org.matsim.*
 * EvacuationScenarioCleaner.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package org.matsim.contrib.grips.visualization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;

public class EvacuationScenarioCleaner {

	private final Scenario scenario;

	public EvacuationScenarioCleaner(Scenario scenario) {
		this.scenario = scenario;
	}

	public void run() {
		
		Iterator<? extends Person> it = this.scenario.getPopulation().getPersons().values().iterator();
		
		while (it.hasNext()) {
			Person pers = it.next();
			boolean success = handle(pers);
			if (!success) {
				it.remove();
			}
		}
		
		Iterator<? extends Link> it2 = this.scenario.getNetwork().getLinks().values().iterator();
		while (it2.hasNext()) {
			Link link = it2.next();
			if (link.getId().toString().equals("el1")){
				;;
			} else if (link.getId().toString().contains("el")){
				handle(link);
			}
		}
		
		
	}

	private void handle(Link link) {
		Id id = this.scenario.createId("en"+link.getId().toString());
		Node n = this.scenario.getNetwork().getFactory().createNode(id, link.getFromNode().getCoord());
		this.scenario.getNetwork().addNode(n);
		link.setToNode(n);
	}

	private boolean handle(Person pers) {
		
		
		Iterator<? extends Plan> it = pers.getPlans().iterator();
		while (it.hasNext()) {
			Plan plan = it.next();
			try {
				handle(plan);
			} catch (Exception e) {
				e.printStackTrace();
				it.remove();
			}
		}
		
		return pers.getPlans().size() > 0;
		
	}

	private void handle(Plan plan) {
		
		Id actLink = null;
		for (PlanElement pel : plan.getPlanElements()) {
			if (pel instanceof LegImpl) {
				actLink = handle((LegImpl)pel);
			}
			if (pel instanceof ActivityImpl) {
				Id id = ((ActivityImpl)pel).getLinkId();
				if (id.toString().contains("el")) {
					handle((ActivityImpl)pel,actLink);
				}
			}
		}
		
	}

	private Id handle(LegImpl l) {

		Id endLinkId;
		
		String m = l.getMode();
		if (m.equals("transit_walk")) {
			throw new RuntimeException("not implemented yet!");
		}
		
		Route r = l.getRoute();
		
		if (r instanceof LinkNetworkRouteImpl) {
			LinkNetworkRouteImpl rr = (LinkNetworkRouteImpl)r;
			List<Id> ll = rr.getLinkIds();
			ArrayList<Id> nll = new ArrayList<Id>(ll.subList(0, ll.size()-1));
			endLinkId = ll.get(ll.size()-1);
			rr.setLinkIds(rr.getStartLinkId(), nll, endLinkId);
			
		} else {
			throw new RuntimeException("Can not handle route instances of type" + r.getClass().getName());
		}
		
		return endLinkId;
	}

	private void handle(ActivityImpl pel, Id actLink) {
		pel.setLinkId(actLink);
	}

}
