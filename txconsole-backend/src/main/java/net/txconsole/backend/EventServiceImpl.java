package net.txconsole.backend;

import lombok.Data;
import net.txconsole.core.model.EventForm;
import net.txconsole.service.EventService;

@Data
public class EventServiceImpl implements EventService {
    @Override
    public void event(EventForm eventForm) {
        // FIXME Creates the event in DB
    }
}
