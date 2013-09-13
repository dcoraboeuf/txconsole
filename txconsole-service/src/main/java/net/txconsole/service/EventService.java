package net.txconsole.service;

import net.txconsole.core.model.Event;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.EventForm;

import java.util.List;

public interface EventService {

    void event(EventForm eventForm);

    Event getEvent(EventEntity entity, int entityId, EventCode eventCode);

    List<Event> getEvents(EventEntity entity, int entityId, EventCode eventCode);

}
