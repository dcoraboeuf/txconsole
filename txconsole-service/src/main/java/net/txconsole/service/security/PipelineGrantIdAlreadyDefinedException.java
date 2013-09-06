package net.txconsole.service.security;

import net.sf.jstring.support.CoreException;

public class PipelineGrantIdAlreadyDefinedException extends CoreException {

    public PipelineGrantIdAlreadyDefinedException(String name, Class<?> annotationClass) {
        super(name, annotationClass.getSimpleName());
    }

}
