package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.RequestStatus;

@Data
public class TRequest {

    private final int id;
    private final int branchId;
    private final String version;
    private final RequestStatus status;

}
