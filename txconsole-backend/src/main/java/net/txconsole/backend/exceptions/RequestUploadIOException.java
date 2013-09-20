package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class RequestUploadIOException extends CoreException {
    public RequestUploadIOException(IOException e, String name) {
        super(e, name);
    }
}
