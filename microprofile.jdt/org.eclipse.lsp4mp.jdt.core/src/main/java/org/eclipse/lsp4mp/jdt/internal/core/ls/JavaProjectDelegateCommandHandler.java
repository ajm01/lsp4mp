/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
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
package org.eclipse.lsp4mp.jdt.internal.core.ls;

import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getFirst;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getString;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getStringList;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4jdt.commons.JavaProjectLabelsParams;
import org.eclipse.lsp4jdt.core.ProjectLabelManager;
import org.eclipse.lsp4jdt.participants.core.ls.AbstractDelegateCommandHandler;
import org.eclipse.lsp4jdt.participants.core.ls.JDTUtilsLSImpl;
import org.eclipse.lsp4mp.jdt.core.MPNewPropertiesManagerForJava;

/**
 * Delegate command handler for Java project information
 *
 */
public class JavaProjectDelegateCommandHandler extends AbstractDelegateCommandHandler {

	private static final String PROJECT_LABELS_COMMAND_ID = "microprofile/java/projectLabels";
	private static final String WORKSPACE_LABELS_COMMAND_ID = "microprofile/java/workspaceLabels";

	public JavaProjectDelegateCommandHandler() {
	}

	@Override
	public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
		switch (commandId) {
		case PROJECT_LABELS_COMMAND_ID:
			return getProjectLabelInfo(arguments, commandId, progress);
		case WORKSPACE_LABELS_COMMAND_ID:
			// not sure using the MPNewPropertiesManagerForJava plugin id is correct?
			return ProjectLabelManager.getInstance().getProjectLabelInfo(MPNewPropertiesManagerForJava.getInstance().getPluginId());
		default:
			throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
		}
	}

	private static Object getProjectLabelInfo(List<Object> arguments, String commandId, IProgressMonitor monitor) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaProjectLabelsParams argument!", commandId));
		}
		// Get project name from the java file URI
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaProjectLabelsParams.uri (java file URI)!",
					commandId));
		}
		List<String> types = getStringList(obj, "types");
		JavaProjectLabelsParams params = new JavaProjectLabelsParams();
		params.setUri(javaFileUri);
		params.setTypes(types);
		return ProjectLabelManager.getInstance().getProjectLabelInfo(params, MPNewPropertiesManagerForJava.getInstance().getPluginId(), JDTUtilsLSImpl.getInstance(), monitor);
	}
}
