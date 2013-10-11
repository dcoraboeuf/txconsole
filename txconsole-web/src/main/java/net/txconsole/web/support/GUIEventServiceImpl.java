package net.txconsole.web.support;

import net.sf.jstring.Strings;
import net.txconsole.core.model.*;
import net.txconsole.core.support.TimeUtils;
import net.txconsole.service.EventService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

@Service
public class GUIEventServiceImpl implements GUIEventService {

    private final Pattern replacementPattern = Pattern.compile("(##(\\d+)(,([A-Z]+))?##)");
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

    @Override
    public String getEventMessage(Locale locale, Event event) {
        // Getting the general pattern from the localization strings
        String canvas = strings.get(locale, "event." + event.getEventCode().name());
        // Replacing the $...$ tokens
        Matcher m = replacementPattern.matcher(canvas);
        StringBuffer html = new StringBuffer();
        while (m.find()) {
            int index = Integer.parseInt(m.group(2), 10);
            String entityName = m.group(4);
            EventEntity entity = null;
            if (StringUtils.isNotBlank(entityName)) {
                entity = EventEntity.valueOf(entityName);
            }
            String value = expandToken(event, index, entity);
            m.appendReplacement(html, value);
        }
        m.appendTail(html);
        return html.toString();
    }

    protected String expandToken(Event event, int index, EventEntity entity) {
        // Gets the parameter value
        String name = escapeHtml4(event.getEventParameters().get(index));
        // Link?
        if (entity != null) {
            // Gets the entity ID
            Integer entityId = event.getEntities().get(entity);
            if (entityId != null) {
                // Gets the href for this entity
                String href = format(
                        "%s/%d",
                        entity.name().toLowerCase(),
                        entityId
                );
                // Link
                return format("<a class=\"event-entity\" href=\"%s\">%s</a>", href, name);
            }
        }
        // Default
        return format("<span class=\"event-entity\">%s</span>", name);
    }
}
