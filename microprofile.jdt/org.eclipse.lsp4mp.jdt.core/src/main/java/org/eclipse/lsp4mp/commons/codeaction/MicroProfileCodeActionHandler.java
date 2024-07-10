package org.eclipse.lsp4mp.commons.codeaction;

import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4jdt.participants.core.java.codeaction.AbstractCodeActionHandler;

public class MicroProfileCodeActionHandler extends AbstractCodeActionHandler {

    /** Singleton JakartaCodeActionHandler instance. */
    public static final MicroProfileCodeActionHandler INSTANCE = new MicroProfileCodeActionHandler();

    /**
     * Returns an instance of JakartaCodeActionHandler.
     *
     * @return An instance of JakartaCodeActionHandler.
     */
    public static MicroProfileCodeActionHandler getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDefaultSupportedKinds() {
        return Arrays.asList(CodeActionKind.QuickFix);
    }

}
