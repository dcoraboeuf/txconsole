package net.txconsole.backend;

import net.txconsole.backend.dao.EventDao;
import net.txconsole.core.model.EventForm;
import net.txconsole.core.model.Signature;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;
    private final SecurityUtils securityUtils;

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

}
