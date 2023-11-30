/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.lspcommon.jdt.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the common plug-in life cycle
 */
public class CommonCorePlugin implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.lspcommon.jdt.core";

    // The shared instance
    private static CommonCorePlugin plugin;

    public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CommonCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Log the given status.
	 * @param status The status to log
	 */
	public static void log(IStatus status) {
        // getDefault().getLog().log(status);
    }

	/**
	 * Log the provided error.
	 * @param errMsg The error message.
	 * @param ex The exception associated with the error.
	 */
	public static void logException(String errMsg, Throwable ex) {
        // getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, errMsg, ex));
    }

}
