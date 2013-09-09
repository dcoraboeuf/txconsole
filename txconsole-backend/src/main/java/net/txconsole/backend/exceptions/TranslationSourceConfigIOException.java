package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class TranslationSourceConfigIOException extends CoreException {
    public TranslationSourceConfigIOException(String id, IOException e) {
        super(e, id, e);
    }
}
