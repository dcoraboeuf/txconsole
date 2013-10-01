package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.RequestStatus;
import net.txconsole.core.support.SimpleMessage;

@Data
public class TRequest {

    private final int id;
    private final int branchId;
    private final String version;
    private final String toVersion;
    private final String mergeVersion;
    private final RequestStatus status;
    private final SimpleMessage message;

}
