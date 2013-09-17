package net.txconsole.core.support;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class IOContextException extends CoreException {
    public IOContextException(IOException e) {
        super(e);
    }
}
