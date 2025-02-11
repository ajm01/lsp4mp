/*******************************************************************************
 * Copyright (c) 2021 Red Hat Inc. and others.
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
package org.eclipse.lsp4mp.commons;

import java.util.Collections;
import java.util.List;

import org.eclipse.lsp4jdt.commons.JavaDiagnosticsSettings;

/**
 * Represents settings used by the MicroProfile JDT component while validating
 * Java class files
 *
 */
public class MicroProfileJavaDiagnosticsSettings extends JavaDiagnosticsSettings {

	public MicroProfileJavaDiagnosticsSettings(List<String> inpatterns) {
		super(inpatterns);
		this.mppatterns = inpatterns;
	}

	private List<String> mppatterns;

	/**
	 * Returns a list of patterns representing the properties to ignore validation
	 * when adding diagnostics for properties without values.
	 *
	 * @return a list of patterns representing the properties to ignore validation
	 *         when adding diagnostics for properties without values.
	 */
	public List<String> getPatterns() {
		return super.getPatterns() == null ? Collections.emptyList() : this.mppatterns;
	}

}
