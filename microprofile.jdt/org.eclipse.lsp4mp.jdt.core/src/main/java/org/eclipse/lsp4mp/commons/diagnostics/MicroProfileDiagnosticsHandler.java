package org.eclipse.lsp4mp.commons.diagnostics;

import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4mp.commons.MicroProfileJavaDiagnosticsParams;
import org.eclipse.lspcommon.commons.utils.JSONUtility;
import org.eclipse.lspcommon.commons.DocumentFormat;
import org.eclipse.lspcommon.jdt.core.operations.java.diagnostics.AbstractDiagnosticsHandler;

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
    	
    	MicroProfileJavaDiagnosticsParams mpJavaDiagnosticsParams = JSONUtility.toModel(diagnosticsParams, MicroProfileJavaDiagnosticsParams.class);
    			//(MicroProfileJavaDiagnosticsParams) diagnosticsParams;
    	return mpJavaDiagnosticsParams.getUris();
    }
    
    @Override
    public DocumentFormat getDocumentFormatFromParams(Object diagnosticsParams) {
    	MicroProfileJavaDiagnosticsParams javaDiagnosticsParams = JSONUtility.toModel(diagnosticsParams, MicroProfileJavaDiagnosticsParams.class);
    			//(MicroProfileJavaDiagnosticsParams) diagnosticsParams;
    	return javaDiagnosticsParams.getDocumentFormat();
    }
}
