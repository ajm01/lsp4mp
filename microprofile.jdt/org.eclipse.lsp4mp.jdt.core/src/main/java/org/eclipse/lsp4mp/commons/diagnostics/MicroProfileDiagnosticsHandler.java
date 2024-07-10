package org.eclipse.lsp4mp.commons.diagnostics;

import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4jdt.commons.utils.JSONUtility;
import org.eclipse.lsp4jdt.participants.core.java.diagnostics.AbstractDiagnosticsHandler;
import org.eclipse.lsp4jdt.commons.DocumentFormat;
import org.eclipse.lsp4jdt.commons.JavaDiagnosticsParams;

public class MicroProfileDiagnosticsHandler extends AbstractDiagnosticsHandler {

    /** Singleton JakartaCodeActionHandler instance. */
    public static final MicroProfileDiagnosticsHandler INSTANCE = new MicroProfileDiagnosticsHandler();

    /**
     * Returns an instance of JakartaCodeActionHandler.
     *
     * @return An instance of JakartaCodeActionHandler.
     */
    public static MicroProfileDiagnosticsHandler getInstance() {
        return INSTANCE;
    }
    
    @Override
    public List<String> getURIsFromParams(Object diagnosticsParams) {
    	
    	JavaDiagnosticsParams mpJavaDiagnosticsParams = JSONUtility.toModel(diagnosticsParams, JavaDiagnosticsParams.class);
    			//(MicroProfileJavaDiagnosticsParams) diagnosticsParams;
    	return mpJavaDiagnosticsParams.getUris();
    }
    
    @Override
    public DocumentFormat getDocumentFormatFromParams(Object diagnosticsParams) {
    	JavaDiagnosticsParams javaDiagnosticsParams = JSONUtility.toModel(diagnosticsParams, JavaDiagnosticsParams.class);
    			//(MicroProfileJavaDiagnosticsParams) diagnosticsParams;
    	return javaDiagnosticsParams.getDocumentFormat();
    }
}
