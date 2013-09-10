package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

import java.util.List;

public class ProjectParametersNotDefinedByBranchException extends InputException {
    public ProjectParametersNotDefinedByBranchException(List<String> parameters) {
        super(parameters);
    }
}
