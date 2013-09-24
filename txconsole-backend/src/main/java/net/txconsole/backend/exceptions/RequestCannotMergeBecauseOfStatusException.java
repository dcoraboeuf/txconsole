package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;
import net.txconsole.core.model.RequestStatus;

public class RequestCannotMergeBecauseOfStatusException extends CoreException {
    public RequestCannotMergeBecauseOfStatusException(RequestStatus actualStatus, RequestStatus expectedStatus) {
        super(actualStatus, expectedStatus);
    }
}
