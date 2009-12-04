/* *********************************************************************** *
 * project: org.matsim.*
 * MatsimPopulationReader.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
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

package org.matsim.core.population;

import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.ScenarioImpl;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.facilities.ActivityFacilities;
import org.matsim.core.facilities.ActivityFacilitiesImpl;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.matsim.knowledges.Knowledges;
import org.matsim.knowledges.KnowledgesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A reader for plans-files of MATSim. This reader recognizes the format of the plans-file and uses
 * the correct reader for the specific plans-version, without manual setting.
 *
 * @author mrieser
 */
public class MatsimPopulationReader extends MatsimXmlParser implements PopulationReader {

	private final static String PLANS    = "plans.dtd"; // a special, inofficial case, handle it like plans_v0
	private final static String PLANS_V0 = "plans_v0.dtd";
	private final static String PLANS_V1 = "plans_v1.dtd";
	private final static String PLANS_V4 = "plans_v4.dtd";
	
	private final Population plans;
	private final Network network;
	private final ActivityFacilities facilities;
	private MatsimXmlParser delegate = null;
	private Knowledges knowledges;

	private static final Logger log = Logger.getLogger(MatsimPopulationReader.class);

	/**
	 * Creates a new reader for MATSim plans (population) files.
	 *
	 * @param plans The data structure where to store the persons with their plans.
	 * @param network The network the plans are linked to, e.g. for routes, locations, ...
	 * @deprecated use {@link #MatsimPopulationReader(ScenarioImpl)}
	 */
	@Deprecated // use MatsimPopulationReader(Scenario)
	public MatsimPopulationReader(final Population plans, final Network network) {
		this.plans = plans;
		this.network = network;
		this.facilities = (ActivityFacilitiesImpl) Gbl.getWorld().getLayer(ActivityFacilitiesImpl.LAYER_TYPE);
		this.knowledges = new KnowledgesImpl();
	}
	
	public MatsimPopulationReader(final Scenario scenario) {
		this.plans = scenario.getPopulation();
		this.network = scenario.getNetwork();
		if (scenario instanceof ScenarioImpl) {
		this.facilities = ((ScenarioImpl) scenario).getActivityFacilities();
		this.knowledges = ((ScenarioImpl) scenario).getKnowledges();
		} else {
			this.facilities = null;
			this.knowledges = null;
		}
	}

	@Override
	public void startTag(final String name, final Attributes atts, final Stack<String> context) {
		this.delegate.startTag(name, atts, context);
	}

	@Override
	public void endTag(final String name, final String content, final Stack<String> context) {
		this.delegate.endTag(name, content, context);
	}

	/**
	 * Parses the specified plans file. This method calls {@link #parse(String)}, but handles all
	 * possible exceptions on its own.
	 *
	 * @param filename The name of the file to parse.
	 */
	public void readFile(final String filename) {
		try {
			parse(filename);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setDoctype(final String doctype) {
		super.setDoctype(doctype);
		if (PLANS_V4.equals(doctype)) {
			this.delegate = new PopulationReaderMatsimV4(this.plans, this.network, this.facilities, this.knowledges);
			log.info("using plans_v4-reader.");
		} 
		else if (PLANS_V1.equals(doctype)) {
			this.delegate = new PopulationReaderMatsimV1(this.plans, this.network);
			log.info("using plans_v1-reader.");
		} else if (PLANS_V0.equals(doctype) || PLANS.equals(doctype)) {
			this.delegate = new PopulationReaderMatsimV0(this.plans, this.network);
			log.info("using plans_v0-reader.");
		} else {
			throw new IllegalArgumentException("Doctype \"" + doctype + "\" not known.");
		}
	}

}
