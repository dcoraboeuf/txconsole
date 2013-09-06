package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

public class ProjectAlreadyExistException extends InputException {

    public ProjectAlreadyExistException(String name) {
        super(name);
    }
}
