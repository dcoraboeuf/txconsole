package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

public class TranslationSourceIDException extends InputException {

    public TranslationSourceIDException(String id) {
        super(id);
    }

}
