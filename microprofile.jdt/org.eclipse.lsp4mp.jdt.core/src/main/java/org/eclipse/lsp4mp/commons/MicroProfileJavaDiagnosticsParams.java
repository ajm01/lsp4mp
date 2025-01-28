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
package org.eclipse.lsp4mp.commons;

import java.util.List;

import org.eclipse.lsp4jdt.commons.DocumentFormat;
import org.eclipse.lsp4jdt.commons.JavaDiagnosticsParams;

/**
 * MicroProfile Java diagnostics parameters.
 *
 * @author Angelo ZERR
 *
 */
public class MicroProfileJavaDiagnosticsParams  extends JavaDiagnosticsParams{

	private MicroProfileJavaDiagnosticsSettings settings;

	public MicroProfileJavaDiagnosticsParams() {
		this(null);
	}

	public MicroProfileJavaDiagnosticsParams(List<String> incoming_uris) {
		this(incoming_uris, null);
	}

	public MicroProfileJavaDiagnosticsParams(List<String> incoming_uris, MicroProfileJavaDiagnosticsSettings settings) {
		super.setUris(incoming_uris);
		this.settings = settings;
	}

	/**
	 * Returns the java file uris list.
	 *
	 * @return the java file uris list.
	 */
	public List<String> getUris() {
		return super.getUris();
	}

	/**
	 * Set the java file uris list.
	 *
	 * @param uris the java file uris list.
	 */
	public void setUris(List<String> incoming_uris) {
		super.setUris(incoming_uris);
		}

	public DocumentFormat getDocumentFormat() {
		return super.getDocumentFormat();
	}

	public void setDocumentFormat(DocumentFormat documentFormat) {
		super.setDocumentFormat(documentFormat);
	}

	/**
	 * Returns the diagnostics settings.
	 *
	 * @return the diagnostics settings
	 */
	public MicroProfileJavaDiagnosticsSettings getSettings() {
		return this.settings;
	}

	/**
	 * Sets the diagnostics settings.
	 *
	 * @param settings the new value for the diagnostics settings
	 */
	public void setSettings(MicroProfileJavaDiagnosticsSettings settings) {
		this.settings = settings;
	}

}