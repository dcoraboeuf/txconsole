package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

public class RequestNoRequestFileException extends CoreException {
    public RequestNoRequestFileException(int requestId) {
        super(requestId);
    }
}
