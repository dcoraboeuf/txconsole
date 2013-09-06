package net.txconsole.service.security;

import net.sf.jstring.support.CoreException;

public class PipelineGrantIdMissingException extends CoreException {

    public PipelineGrantIdMissingException(String name) {
        super(name);
    }
}
