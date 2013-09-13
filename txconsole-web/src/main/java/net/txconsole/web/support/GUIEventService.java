package net.txconsole.web.support;

import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.web.resource.ResourceEvent;

import java.util.Locale;

public interface GUIEventService {

    ResourceEvent getResourceEvent(Locale locale, EventEntity entity, int entityId, EventCode eventCode);
}
