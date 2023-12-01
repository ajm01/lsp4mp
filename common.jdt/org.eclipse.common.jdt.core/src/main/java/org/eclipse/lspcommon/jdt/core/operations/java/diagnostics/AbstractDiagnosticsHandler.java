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
package org.eclipse.lspcommon.jdt.core.operations.java.diagnostics;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.manipulation.CoreASTProvider;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lspcommon.DocumentFormat;
import org.eclipse.lspcommon.JavaCodeActionParams;
import org.eclipse.lspcommon.JavaDiagnosticsParams;
import org.eclipse.lspcommon.core.codeaction.CodeActionResolveData;
import org.eclipse.lspcommon.jdt.core.java.JavaFeaturesRegistry;
import org.eclipse.lspcommon.jdt.core.java.codeaction.ExtendedCodeAction;
import org.eclipse.lspcommon.jdt.core.java.codeaction.IJavaCodeActionParticipant;
import org.eclipse.lspcommon.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lspcommon.jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lspcommon.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lspcommon.jdt.core.operations.java.corrections.DiagnosticsHelper;
import org.eclipse.lspcommon.jdt.core.utils.IJDTUtils;

/**
 * Code action handler.
 *
 * @author Angelo ZERR
 *
 */
public abstract class AbstractDiagnosticsHandler {

    /**
     * Returns diagnostics for the given uris list.
     *
     * @param params the diagnostics parameters
     * @param utils the utilities class
     * @return diagnostics for the given uris list.
     * @throws JavaModelException
     */
    public List<PublishDiagnosticsParams> diagnostics(String pluginId, Object params, IJDTUtils utils,
                                                      IProgressMonitor monitor) throws JavaModelException {
    	
    	//Step1
    	List<String> uris = getURIsFromParams(params);
    	if (uris == null) {
            return Collections.emptyList();
        }
          
        //Step 2
        DocumentFormat documentFormat = getDocumentFormatFromParams(params);
        
        //Step 3
        return getPublishDiagnosticsParamsList(pluginId, uris, documentFormat, utils, monitor);
    }
    
    public List<String> getURIsFromParams(Object diagnosticsParams) {
    	JavaDiagnosticsParams javaDiagnosticsParams = (JavaDiagnosticsParams) diagnosticsParams;
    	return javaDiagnosticsParams.getUris();
    }
    
    public DocumentFormat getDocumentFormatFromParams(Object diagnosticsParams) {
    	JavaDiagnosticsParams javaDiagnosticsParams = (JavaDiagnosticsParams) diagnosticsParams;
    	return javaDiagnosticsParams.getDocumentFormat();
    }
    
    public List<PublishDiagnosticsParams> getPublishDiagnosticsParamsList(String pluginId, List<String> uris, DocumentFormat docFormat, IJDTUtils utils, IProgressMonitor monitor){
        List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
        
        for (String uri : uris) {
            List<Diagnostic> diagnostics = new ArrayList<>();
            PublishDiagnosticsParams publishDiagnostic = new PublishDiagnosticsParams(uri, diagnostics);
            publishDiagnostics.add(publishDiagnostic);
            collectDiagnostics(pluginId, uri, utils, docFormat, diagnostics, monitor);
        }
        if (monitor.isCanceled()) {
            return Collections.emptyList();
        }
        return publishDiagnostics;
    }
    
    protected void collectDiagnostics(String pluginId, String uri, IJDTUtils utils, DocumentFormat documentFormat, List<Diagnostic> diagnostics, IProgressMonitor monitor) {
        ITypeRoot typeRoot = resolveTypeRoot(uri, utils, monitor);
        if (typeRoot == null) {
            return;
        }

        // Collect all adapted diagnostics participant
        JavaDiagnosticsContext context = new JavaDiagnosticsContext(uri, typeRoot, utils, documentFormat);
        List<JavaDiagnosticsDefinition> definitions = JavaFeaturesRegistry.getInstance().getJavaDiagnosticsDefinitions(pluginId).stream().filter(definition -> definition.isAdaptedForDiagnostics(context,
                                                                                                                                                                                                       monitor)).collect(Collectors.toList());
        if (definitions.isEmpty()) {
            return;
        }

        // Begin, collect, end participants
        definitions.forEach(definition -> definition.beginDiagnostics(context, monitor));
        definitions.forEach(definition -> {
            List<Diagnostic> collectedDiagnostics = definition.collectDiagnostics(context, monitor);
            if (collectedDiagnostics != null && !collectedDiagnostics.isEmpty()) {
                diagnostics.addAll(collectedDiagnostics);
            }
        });
        definitions.forEach(definition -> definition.endDiagnostics(context, monitor));
    }

	/**
     * Given the uri returns a {@link ITypeRoot}. May return null if it can not
     * associate the uri with a Java file or class file.
     *
     * @param uri
     * @param utils JDT LS utilities
     * @param monitor the progress monitor
     * @return compilation unit
     */
    private static ITypeRoot resolveTypeRoot(String uri, IJDTUtils utils, IProgressMonitor monitor) {
        utils.waitForLifecycleJobs(monitor);
        final ICompilationUnit unit = utils.resolveCompilationUnit(uri);
        IClassFile classFile = null;
        if (unit == null) {
            classFile = utils.resolveClassFile(uri);
            if (classFile == null) {
                return null;
            }
        } else {
            if (!unit.getResource().exists() || monitor.isCanceled()) {
                return null;
            }
        }
        return unit != null ? unit : classFile;
    }
}
