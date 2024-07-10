/*******************************************************************************
* Copyright (c) 2019-2020 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.jdt.internal.core.ls;

import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getBoolean;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getCodeActionContext;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getFirst;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getInt;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getObject;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getPosition;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getRange;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getString;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getStringList;
import static org.eclipse.lsp4jdt.participants.core.ls.ArgumentUtils.getTextDocumentIdentifier;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4jdt.commons.DocumentFormat;
import org.eclipse.lsp4jdt.commons.JavaCursorContextKind;
import org.eclipse.lsp4jdt.commons.JavaCursorContextResult;
import org.eclipse.lsp4jdt.commons.JavaFileInfo;
import org.eclipse.lsp4mp.commons.MicroProfileDefinition;
import org.eclipse.lsp4jdt.commons.JavaCodeActionParams;
import org.eclipse.lsp4jdt.commons.JavaCodeLensParams;
import org.eclipse.lsp4jdt.commons.JavaCompletionParams;
import org.eclipse.lsp4jdt.commons.JavaCompletionResult;
import org.eclipse.lsp4jdt.commons.JavaDefinitionParams;
import org.eclipse.lsp4jdt.commons.JavaDiagnosticsParams;
import org.eclipse.lsp4jdt.commons.JavaDiagnosticsSettings;
import org.eclipse.lsp4jdt.commons.JavaFileInfoParams;
import org.eclipse.lsp4jdt.commons.JavaHoverParams;
import org.eclipse.lsp4jdt.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jdt.participants.core.ls.AbstractDelegateCommandHandler;
import org.eclipse.lsp4jdt.participants.core.ls.JDTUtilsLSImpl;
import org.eclipse.lsp4mp.commons.utils.JSONUtility;
import org.eclipse.lsp4mp.jdt.core.MPNewPropertiesManagerForJava;

/**
 * JDT LS delegate command handler for Java file.
 *
 * @author Angelo ZERR
 *
 */
public class MicroProfileDelegateCommandHandlerForJava extends AbstractDelegateCommandHandler {

	private static final String FILE_INFO_COMMAND_ID = "microprofile/java/fileInfo";
	private static final String JAVA_CODEACTION_COMMAND_ID = "microprofile/java/codeAction";
	private static final String JAVA_CODEACTION_RESOLVE_COMMAND_ID = "microprofile/java/codeActionResolve";
	private static final String JAVA_CODELENS_COMMAND_ID = "microprofile/java/codeLens";
	private static final String JAVA_COMPLETION_COMMAND_ID = "microprofile/java/completion";
	private static final String JAVA_DEFINITION_COMMAND_ID = "microprofile/java/definition";
	private static final String JAVA_DIAGNOSTICS_COMMAND_ID = "microprofile/java/diagnostics";
	private static final String JAVA_HOVER_COMMAND_ID = "microprofile/java/hover";
	private static final String JAVA_WORKSPACE_SYMBOLS_ID = "microprofile/java/workspaceSymbols";

	public MicroProfileDelegateCommandHandlerForJava() {
	}

	@Override
	public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
		switch (commandId) {
			case FILE_INFO_COMMAND_ID:
				return getFileInfo(arguments, commandId, progress);
			case JAVA_CODEACTION_COMMAND_ID:
				return getCodeActionForJava(arguments, commandId, progress);
			case JAVA_CODEACTION_RESOLVE_COMMAND_ID:
				return resolveCodeActionForJava(arguments, commandId, progress);
			case JAVA_CODELENS_COMMAND_ID:
				return getCodeLensForJava(arguments, commandId, progress);
			case JAVA_COMPLETION_COMMAND_ID:
				return getCompletionForJava(arguments, commandId, progress);
			case JAVA_DEFINITION_COMMAND_ID:
				return getDefinitionForJava(arguments, commandId, progress);
			case JAVA_DIAGNOSTICS_COMMAND_ID:
				return getDiagnosticsForJava(arguments, commandId, progress);
			case JAVA_HOVER_COMMAND_ID:
				return getHoverForJava(arguments, commandId, progress);
			case JAVA_WORKSPACE_SYMBOLS_ID:
				return getWorkspaceSymbolsForJava(arguments, commandId, progress);
			default:
				throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
		}
	}

	/**
	 * Returns the file information (package name, etc) for the given Java file.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the file information (package name, etc) for the given Java file.
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	private static JavaFileInfo getFileInfo(List<Object> arguments, String commandId, IProgressMonitor monitor)
			throws JavaModelException, CoreException {
		// Create java file information parameter
		JavaFileInfoParams params = createJavaFileInfoParams(arguments, commandId);
		// Return file information from the parameter
		return MPNewPropertiesManagerForJava.getInstance().fileInfo(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Create the Java file information parameter from the given arguments map.
	 *
	 * @param arguments
	 * @param commandId
	 *
	 * @return the Java file information parameter.
	 */
	private static JavaFileInfoParams createJavaFileInfoParams(List<Object> arguments, String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaFileInfoParams argument!", commandId));
		}
		// Get project name from the java file URI
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaFileInfoParams.uri (java file URI)!",
					commandId));
		}
		JavaFileInfoParams params = new JavaFileInfoParams();
		params.setUri(javaFileUri);
		return params;
	}

	/**
	 * Returns the code action for the given Java file.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the code action for the given Java file.
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	private static List<? extends CodeAction> getCodeActionForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		// Create java code action parameter
		JavaCodeActionParams params = createMicroProfileJavaCodeActionParams(arguments, commandId);
		// Return code action from the code action parameter
		return MPNewPropertiesManagerForJava.getInstance().codeAction(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Create java code action parameter from the given arguments map.
	 *
	 * @param arguments
	 * @param commandId
	 *
	 * @return java code action parameter
	 */
	private static JavaCodeActionParams createMicroProfileJavaCodeActionParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaCodeActionParams argument!", commandId));
		}
		TextDocumentIdentifier texdDocumentIdentifier = getTextDocumentIdentifier(obj, "textDocument");
		if (texdDocumentIdentifier == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaCodeActionParams.texdDocumentIdentifier",
					commandId));
		}
		Range range = getRange(obj, "range");
		CodeActionContext context = getCodeActionContext(obj, "context");
		boolean resourceOperationSupported = getBoolean(obj, "resourceOperationSupported");
		boolean commandConfigurationUpdateSupported = getBoolean(obj, "commandConfigurationUpdateSupported");
		boolean resolveSupported = getBoolean(obj, "resolveSupported");
		JavaCodeActionParams params = new JavaCodeActionParams();
		params.setTextDocument(texdDocumentIdentifier);
		params.setRange(range);
		params.setContext(context);
		params.setResourceOperationSupported(resourceOperationSupported);
		params.setCommandConfigurationUpdateSupported(commandConfigurationUpdateSupported);
		params.setResolveSupported(resolveSupported);
		return params;
	}

	private static CodeAction resolveCodeActionForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		// Create java code action parameter
		CodeAction unresolved = createMicroProfileJavaCodeActionResolveParams(arguments, commandId);
		// Return code action from the code action parameter
		return MPNewPropertiesManagerForJava.getInstance().resolveCodeAction(unresolved, JDTUtilsLSImpl.getInstance(),
				monitor);
	}

	private static CodeAction createMicroProfileJavaCodeActionResolveParams(List<Object> arguments, String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one CodeAction argument!", commandId));
		}
		CodeAction codeAction = JSONUtility.toModel(obj, CodeAction.class);
		if (codeAction == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one CodeAction argument!", commandId));
		}
		CodeActionResolveData resolveData = JSONUtility.toModel(codeAction.getData(), CodeActionResolveData.class);
		if (resolveData == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with a CodeAction that has CodeActionResolveData!", commandId));
		}
		codeAction.setData(resolveData);
		return codeAction;
	}

	/**
	 * Returns the code lenses for the given Java file.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the code lenses for the given Java file.
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	private static List<? extends CodeLens> getCodeLensForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		// Create java code lens parameter
		JavaCodeLensParams params = createMicroProfileJavaCodeLensParams(arguments, commandId);
		// Return code lenses from the lens parameter
		return MPNewPropertiesManagerForJava.getInstance().codeLens(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Create java code lens parameter from the given arguments map.
	 *
	 * @param arguments
	 * @param commandId
	 *
	 * @return java code lens parameter
	 */
	private static JavaCodeLensParams createMicroProfileJavaCodeLensParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaCodeLensParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaCodeLensParams.uri (java URI)!",
					commandId));
		}
		JavaCodeLensParams params = new JavaCodeLensParams(javaFileUri);
		params.setUrlCodeLensEnabled(getBoolean(obj, "urlCodeLensEnabled"));
		params.setCheckServerAvailable(getBoolean(obj, "checkServerAvailable"));
		params.setOpenURICommand(getString(obj, "openURICommand"));
		params.setLocalServerPort(getInt(obj, "localServerPort"));
		return params;
	}

	/**
	 * Return the completion result for the given arguments
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the completion result for the given arguments
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private static JavaCompletionResult getCompletionForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		JavaCompletionParams params = createJavaCompletionParams(arguments, commandId);
		CompletionList completionList = MPNewPropertiesManagerForJava.getInstance().completion(params, JDTUtilsLSImpl.getInstance(), monitor);
		JavaCursorContextResult cursorContext = MPNewPropertiesManagerForJava.getInstance().javaCursorContext(params, JDTUtilsLSImpl.getInstance(), monitor);
		return new JavaCompletionResult(completionList, cursorContext);
	}

	/**
	 * Create the completion parameters from the given argument map
	 *
	 * @param arguments
	 * @param commandId
	 * @return the completion parameters from the given argument map
	 */
	private static JavaCompletionParams createJavaCompletionParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaCompletionParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaCompletionParams.uri (java URI)!",
					commandId));
		}
		Position position = getPosition(obj, "position");
		if (position == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaCompletionParams.position (completion trigger location)!",
					commandId));
		}
		JavaCompletionParams params = new JavaCompletionParams(javaFileUri, position);
		return params;
	}

	/**
	 * Returns the list o <code>MicroProfileLocationLink</code> for the definition
	 * described in <code>arguments</code>
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private static List<Object> getDefinitionForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		// Create java definition parameter
		JavaDefinitionParams params = createMicroProfileJavaDefinitionParams(arguments, commandId);
		// Return hover info from hover parameter
		return MPNewPropertiesManagerForJava.getInstance().definition(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Returns the java definition parameters from the given arguments map.
	 *
	 * @param arguments
	 * @param commandId
	 *
	 * @return the definition hover parameters
	 */
	private static JavaDefinitionParams createMicroProfileJavaDefinitionParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaDefinitionParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaDefinitionParams.uri (java URI)!",
					commandId));
		}

		Position hoverPosition = getPosition(obj, "position");
		return new JavaDefinitionParams(javaFileUri, hoverPosition);
	}

	/**
	 * Returns the publish diagnostics list for a given java file URIs.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the publish diagnostics list for a given java file URIs.
	 * @throws JavaModelException
	 */
	private static List<PublishDiagnosticsParams> getDiagnosticsForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException {
		// Create java diagnostics parameter
		JavaDiagnosticsParams params = createMicroProfileJavaDiagnosticsParams(arguments, commandId);
		// Return diagnostics from parameter
		return MPNewPropertiesManagerForJava.getInstance().diagnostics(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Returns the java diagnostics parameters from the given arguments map.
	 *
	 * @param arguments
	 * @param commandId
	 *
	 * @return the java diagnostics parameters
	 */
	private static JavaDiagnosticsParams createMicroProfileJavaDiagnosticsParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one MicroProfileJavaDiagnosticsParams argument!", commandId));
		}
		List<String> javaFileUri = getStringList(obj, "uris");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaDiagnosticsParams.uri (java URIs)!",
					commandId));
		}
		JavaDiagnosticsSettings settings = null;
		Map<String, Object> settingsObj = getObject(obj, "settings");
		if (settingsObj != null) {
			List<String> patterns = getStringList(settingsObj, "patterns");
			settings = new JavaDiagnosticsSettings(patterns);
		}
		//return new JavaDiagnosticsParams(javaFileUri, settings);
		return new JavaDiagnosticsParams(javaFileUri);
	}

	/**
	 * Returns the <code>Hover</code> for the hover described in
	 * <code>arguments</code>
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private static Hover getHoverForJava(List<Object> arguments, String commandId, IProgressMonitor monitor)
			throws JavaModelException, CoreException {
		// Create java hover parameter
		JavaHoverParams params = createMicroProfileJavaHoverParams(arguments, commandId);
		// Return hover info from hover parameter
		return MPNewPropertiesManagerForJava.getInstance().hover(params, JDTUtilsLSImpl.getInstance(), monitor);
	}

	/**
	 * Returns the java hover parameters from the given arguments map.
	 *
	 * @param arguments
	 * @param commandId
	 *
	 * @return the java hover parameters
	 */
	private static JavaHoverParams createMicroProfileJavaHoverParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String
					.format("Command '%s' must be called with one MicroProfileJavaHoverParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required MicroProfileJavaHoverParams.uri (java URI)!",
					commandId));
		}

		Position hoverPosition = getPosition(obj, "position");
		DocumentFormat documentFormat = DocumentFormat.PlainText;
		Number documentFormatIndex = (Number) obj.get("documentFormat");
		if (documentFormatIndex != null) {
			documentFormat = DocumentFormat.forValue(documentFormatIndex.intValue());
		}
		boolean surroundEqualsWithSpaces = ((Boolean) obj.get("surroundEqualsWithSpaces")).booleanValue();
		return new JavaHoverParams(javaFileUri, hoverPosition, documentFormat, surroundEqualsWithSpaces);
	}

	private List<SymbolInformation> getWorkspaceSymbolsForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) {
		String projectUri = createMicroProfileJavaWorkspaceSymbolParams(arguments, commandId);
		return MPNewPropertiesManagerForJava.getInstance().workspaceSymbols(projectUri, JDTUtilsLSImpl.getInstance(),
				monitor);
	}

	private static String createMicroProfileJavaWorkspaceSymbolParams(List<Object> arguments, String commandId) {
		Object projectUriObj = (String) arguments.get(0);
		if (projectUriObj == null || !(projectUriObj instanceof String)) {
			throw new UnsupportedOperationException(String
					.format("Command '%s' must be called with one projectUri: String argument!", commandId));
		}
		return (String)projectUriObj;
	}

}
