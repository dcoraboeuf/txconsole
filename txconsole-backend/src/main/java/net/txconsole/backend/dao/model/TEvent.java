package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class TEvent {

    private final int id;
    private final EventCode eventCode;
    private final Collection<String> eventParameters;
    private final DateTime eventTimestamp;
    private final Integer accountId;
    private final String accountName;
    private final Map<EventEntity, Integer> entities = new HashMap<>();

    public TEvent withEntity(EventEntity eventEntity, Integer eventEntityId) {
        entities.put(eventEntity, eventEntityId);
        return this;
    }
}
