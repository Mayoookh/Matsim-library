/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package org.matsim.contrib.dvrp.run;

import org.matsim.contrib.dynagent.run.DynActivityEngine;
import org.matsim.core.mobsim.qsim.PreplanningEngineQSimModule;
import org.matsim.core.mobsim.qsim.components.QSimComponentsConfigurator;

/**
 * @author Michal Maciejewski (michalm)
 */
public class DvrpQSimComponents {
	public static QSimComponentsConfigurator activateModes(String... modes) {
		return components -> {
			components.addNamedComponent(DynActivityEngine.COMPONENT_NAME);
			components.addNamedComponent(PreplanningEngineQSimModule.COMPONENT_NAME);
			MultiModals.requireAllModesUnique(modes);
			for (String m : modes) {
				components.addComponent(DvrpModes.mode(m));
			}
		};
	}

	public static QSimComponentsConfigurator activateAllModes(MultiModal<?> multiModal) {
		return activateModes(multiModal.modes().toArray(String[]::new));
	}
}
