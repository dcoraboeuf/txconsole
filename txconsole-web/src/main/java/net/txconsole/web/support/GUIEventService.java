package net.txconsole.web.support;

import net.txconsole.core.model.Event;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.ResourceEvent;
import org.joda.time.DateTime;

import java.util.Locale;

public interface GUIEventService {

    ResourceEvent getResourceEvent(Locale locale, EventEntity entity, int entityId, EventCode eventCode);

    ResourceEvent getResourceEvent(Locale locale, String author, DateTime timestamp, EventCode eventCode);

    String getEventMessage(Locale locale, Event event);
}
