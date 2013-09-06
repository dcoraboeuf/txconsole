package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

public class PipelineAlreadyExistException extends InputException {

    public PipelineAlreadyExistException(String name) {
        super(name);
    }
}
