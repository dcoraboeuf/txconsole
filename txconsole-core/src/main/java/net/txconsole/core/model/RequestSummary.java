package net.txconsole.core.model;

import lombok.Data;

@Data
public class RequestSummary {

    private final int id;
    private final int branchId;
    private final String version;
    private final RequestStatus status;

    public boolean isDownloadable() {
        return status == RequestStatus.REQUEST_EXPORTED;
    }
}
