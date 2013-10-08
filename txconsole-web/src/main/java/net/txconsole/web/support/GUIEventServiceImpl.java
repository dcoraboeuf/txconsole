package net.txconsole.web.support;

import net.sf.jstring.Strings;
import net.txconsole.core.model.*;
import net.txconsole.core.support.TimeUtils;
import net.txconsole.service.EventService;
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
        return getResourceEvent(locale, signature.getAuthorName(), signature.getTimestamp(), eventCode);
    }

    @Override
    public ResourceEvent getResourceEvent(Locale locale, String author, DateTime timestamp, EventCode eventCode) {
        DateTime now = TimeUtils.now();
        return new ResourceEvent(
                eventCode,
                author,
                TimeUtils.format(locale, timestamp),
                TimeUtils.elapsed(strings, locale, timestamp, now),
                TimeUtils.elapsed(strings, locale, timestamp, now, author)
        );
    }
}
