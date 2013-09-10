package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

import java.util.List;

public class ProjectParametersNotDefinedException extends InputException {
    public ProjectParametersNotDefinedException(List<String> parameters) {
        super(parameters);
    }
}
