package net.txconsole.web.resource;

import lombok.Data;
import net.txconsole.core.model.Event;
import net.txconsole.core.model.Resource;
import net.txconsole.core.model.ResourceEvent;

@Data
public class EventResource extends Resource<Event> {

    private final String message;

    public EventResource(Event e, ResourceEvent resourceEvent, String message) {
        super(e);
        withEvent(resourceEvent);
        this.message = message;
    }

}
