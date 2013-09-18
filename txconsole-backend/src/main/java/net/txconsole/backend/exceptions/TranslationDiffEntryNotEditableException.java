package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

public class TranslationDiffEntryNotEditableException extends CoreException {
    public TranslationDiffEntryNotEditableException(String key) {
        super(key);
    }
}
