package net.txconsole.web.resource;

import lombok.Data;
import net.txconsole.core.model.Event;
import net.txconsole.core.model.Resource;

@Data
public class EventResource extends Resource<Event> {

    public EventResource(Event e) {
        super(e);
    }

}
