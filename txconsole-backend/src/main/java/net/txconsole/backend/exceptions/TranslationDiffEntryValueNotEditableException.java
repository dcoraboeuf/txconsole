package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

import java.util.Locale;

public class TranslationDiffEntryValueNotEditableException extends CoreException {
    public TranslationDiffEntryValueNotEditableException(String key, Locale locale) {
        super(key, locale);
    }
}
