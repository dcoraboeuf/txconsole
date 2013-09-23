package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

public class TranslationDiffEntryNotFoundException extends InputException {
    public TranslationDiffEntryNotFoundException(String bundle, String section, String key) {
        super(bundle, section, key);
    }
}
