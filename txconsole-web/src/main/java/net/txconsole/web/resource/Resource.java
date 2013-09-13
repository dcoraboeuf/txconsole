package net.txconsole.web.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.txconsole.core.security.SecurityFunction;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class Resource<T> extends ResourceSupport {

    public static final String REL_GUI = "gui";
    private final T data;
    private final Set<String> actions = new HashSet<>();
    private final Collection<ResourceEvent> events = new ArrayList<>();

    public Resource<T> withLink(Link link) {
        add(link);
        return this;
    }

    public Resource<T> withAction(SecurityFunction action) {
        actions.add(action.getCategory().name() + "#" + action.name());
        return this;
    }

    public Resource<T> withAction(SecurityFunction action, boolean ok) {
        if (ok) {
            return withAction(action);
        } else {
            return this;
        }
    }

    public Resource<T> withEvent(ResourceEvent event) {
        events.add(event);
        return this;
    }

}
