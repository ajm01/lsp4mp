package org.eclipse.lsp4mp.jdt.core;

import org.eclipse.lsp4mp.commons.codeaction.MicroProfileCodeActionHandler;
import org.eclipse.lsp4mp.commons.diagnostics.MicroProfileDiagnosticsHandler;
import org.eclipse.lsp4jdt.core.AbstractPropertiesManagerForJava;
import org.eclipse.lsp4jdt.participants.core.java.codeaction.AbstractCodeActionHandler;
import org.eclipse.lsp4jdt.participants.core.java.diagnostics.AbstractDiagnosticsHandler;

public class MPNewPropertiesManagerForJava extends AbstractPropertiesManagerForJava {

	public static final String PLUGIN_ID = "org.eclipse.lsp4mp.jdt.core"; //$NON-NLS-1$
	
	MPNewPropertiesManagerForJava() {
		super();
		//TODO Auto-generated constructor stub
	}

	private static final AbstractPropertiesManagerForJava INSTANCE = new MPNewPropertiesManagerForJava();

	public static AbstractPropertiesManagerForJava getInstance() {
		return INSTANCE;
	}


	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}


	@Override
	public AbstractCodeActionHandler getCodeActionHandler() {
		return new MicroProfileCodeActionHandler();
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractDiagnosticsHandler getDiagnosticsHandler() {
        return MicroProfileDiagnosticsHandler.getInstance();
    }
}
