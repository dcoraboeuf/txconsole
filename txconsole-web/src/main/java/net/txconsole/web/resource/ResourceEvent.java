package net.txconsole.web.resource;

import lombok.Data;
import net.txconsole.core.model.EventCode;

@Data
public class ResourceEvent {

    private final EventCode code;
    // TODO private final String htmlMessage;
    private final String author;
    private final String formattedTimestamp;
    private final String elapsedTime;

}
