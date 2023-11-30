/*******************************************************************************
* Copyright (c) 2020, 2023 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lspcommon.jdt.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Registry to hold the extension point
 * "org.eclipse.lsp4mp.jdt.core.projectLabelProviders".
 *
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/ProjectLabelRegistry.java
 *
 */
public class ProjectLabelRegistry {

    private static final String CLASS_ATTR = "class";

    private static final String EXTENSION_PROJECT_LABEL_PROVIDERS = "projectLabelProviders";

    private static final Logger LOGGER = Logger.getLogger(ProjectLabelRegistry.class.getName());

    private static final ProjectLabelRegistry INSTANCE = new ProjectLabelRegistry();

    private final List<ProjectLabelDefinition> projectLabelDefinitions;

    private boolean projectDefinitionsLoaded;

    public static ProjectLabelRegistry getInstance() {
        return INSTANCE;
    }

    public ProjectLabelRegistry() {
        projectDefinitionsLoaded = false;
        projectLabelDefinitions = new ArrayList<>();
    }

    /**
     * Returns a list of project label definitions
     *
     * @return a list of project label definitions
     */
    public List<ProjectLabelDefinition> getProjectLabelDefinitions(String pluginId) {
        loadProjectLabelDefinitions(pluginId);
        return projectLabelDefinitions;
    }

    private synchronized void loadProjectLabelDefinitions(String pluginId) {
        if (projectDefinitionsLoaded)
            return;

        // Immediately set the flag, as to ensure that this method is never
        // called twice
        projectDefinitionsLoaded = true;

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] cf = registry.getConfigurationElementsFor(pluginId,
                                                                          EXTENSION_PROJECT_LABEL_PROVIDERS);
        addProjectLabelDefinition(cf);
    }

    private void addProjectLabelDefinition(IConfigurationElement[] cf) {
        for (IConfigurationElement ce : cf) {
            try {
                IProjectLabelProvider provider = (IProjectLabelProvider) ce.createExecutableExtension(CLASS_ATTR);
                synchronized (projectLabelDefinitions) {
                    this.projectLabelDefinitions.add(new ProjectLabelDefinition(provider));
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, "Error while collecting project label extension contributions", t);
            }
        }
    }
}