package net.txconsole.core.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Event {

    private final int id;
    private final EventCode eventCode;
    private final List<String> eventParameters;
    private final Signature signature;
    private final Map<EventEntity, Integer> entities;
}
