package net.txconsole.core.model;

import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class Event {

    private final int id;
    private final EventCode eventCode;
    private final Collection<String> eventParameters;
    private final Signature signature;
    private final Map<EventEntity, Integer> entities;
}
