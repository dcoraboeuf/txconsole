package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.EventDao;
import net.txconsole.backend.dao.model.TEvent;
import net.txconsole.core.model.*;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;
    private final SecurityUtils securityUtils;
    /**
     * Event
     */
    private final Function<TEvent, Event> eventFn = new Function<TEvent, Event>() {
        @Override
        public Event apply(TEvent t) {
            return new Event(
                    t.getId(),
                    t.getEventCode(),
                    t.getEventParameters(),
                    new Signature(t.getAccountId(), t.getAccountName(), t.getEventTimestamp()),
                    t.getEntities()
            );
        }
    };

    @Autowired
    public EventServiceImpl(EventDao eventDao, SecurityUtils securityUtils) {
        this.eventDao = eventDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional
    public void event(EventForm form) {
        // Gets the signature for this event
        Signature signature = securityUtils.getCurrentSignature();
        // Creates the event in DB
        eventDao.add(
                form.getCode(),
                form.getParameters(),
                signature,
                form.getEntities()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEvent(EventEntity entity, int entityId, EventCode eventCode) {
        List<Event> events = getEvents(entity, entityId, eventCode);
        if (events.isEmpty()) {
            return null;
        } else {
            return events.get(0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(EventEntity entity, int entityId, EventCode eventCode) {
        return Lists.transform(
                eventDao.findByEntityAndCode(entity, entityId, eventCode),
                eventFn
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(EventEntity entity, int entityId, int offset, int count) {
        return Lists.transform(
                eventDao.findByEntity(entity, entityId, offset, count),
                eventFn
        );
    }

}
