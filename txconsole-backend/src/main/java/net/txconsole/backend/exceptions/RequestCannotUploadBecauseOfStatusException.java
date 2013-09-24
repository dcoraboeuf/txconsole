package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;
import net.txconsole.core.model.RequestStatus;

public class RequestCannotUploadBecauseOfStatusException extends CoreException {
    public RequestCannotUploadBecauseOfStatusException(RequestStatus actualStatus, RequestStatus expectedStatus) {
        super(actualStatus, expectedStatus);
    }
}
