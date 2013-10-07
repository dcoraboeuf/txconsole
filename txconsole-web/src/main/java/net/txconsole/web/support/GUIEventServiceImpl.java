package net.txconsole.web.support;

import net.sf.jstring.Strings;
import net.txconsole.core.model.Event;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.Signature;
import net.txconsole.core.support.TimeUtils;
import net.txconsole.service.EventService;
import net.txconsole.core.model.ResourceEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class GUIEventServiceImpl implements GUIEventService {

    private final EventService eventService;
    private final Strings strings;

    @Autowired
    public GUIEventServiceImpl(EventService eventService, Strings strings) {
        this.eventService = eventService;
        this.strings = strings;
    }

    @Override
    public ResourceEvent getResourceEvent(Locale locale, EventEntity entity, int entityId, EventCode eventCode) {
        // Gets the event
        Event event = eventService.getEvent(entity, entityId, eventCode);
        if (event == null) return null;
        // Converts the event
        Signature signature = event.getSignature();
        DateTime now = TimeUtils.now();
        ResourceEvent re = new ResourceEvent(
                event.getEventCode(),
                signature.getAuthorName(),
                TimeUtils.format(locale, signature.getTimestamp()),
                TimeUtils.elapsed(strings, locale, signature.getTimestamp(), now),
                TimeUtils.elapsed(strings, locale, signature.getTimestamp(), now, signature.getAuthorName())
        );
        // OK
        return re;
    }
}
