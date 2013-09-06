package net.txconsole.service.security;

import net.sf.jstring.support.CoreException;

public class ProjectGrantIdAlreadyDefinedException extends CoreException {

    public ProjectGrantIdAlreadyDefinedException(String name, Class<?> annotationClass) {
        super(name, annotationClass.getSimpleName());
    }

}
