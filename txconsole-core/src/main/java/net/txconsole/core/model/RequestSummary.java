package net.txconsole.core.model;

import lombok.Data;
import net.txconsole.core.support.SimpleMessage;

@Data
public class RequestSummary {

    private final int id;
    private final int branchId;
    private final String version;
    private final RequestStatus status;
    private final SimpleMessage message;

    public boolean isDownloadable() {
        return status == RequestStatus.EXPORTED;
    }
}
