package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

public class ProjectParameterNotDefinedException extends CoreException {
    public ProjectParameterNotDefinedException(String variable) {
        super(variable);
    }
}
