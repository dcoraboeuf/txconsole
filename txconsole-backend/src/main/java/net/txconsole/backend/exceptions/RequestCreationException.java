package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

public class RequestCreationException extends CoreException {
    public RequestCreationException(int requestId, Exception ex) {
        super(ex, requestId, ex);
    }
}
