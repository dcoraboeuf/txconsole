package net.txconsole.web.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class Resource<T> extends ResourceSupport {

    public static final String REL_GUI = "gui";

    private final T data;
    private final Set<ResourceAction> actions = new HashSet<>();

    public Resource<T> withLink(Link link) {
        add(link);
        return this;
    }

    public Resource<T> withAction(ResourceAction action) {
        actions.add(action);
        return this;
    }

    public Resource<T> withAction(ResourceAction action, boolean ok) {
        if (ok) {
            return withAction(action);
        } else {
            return this;
        }
    }

    public Resource<T> withUpdate(boolean ok) {
        return withAction(ResourceAction.UPDATE, ok);
    }

    public Resource<T> withDelete(boolean ok) {
        return withAction(ResourceAction.DELETE, ok);
    }

    public Resource<T> withUpdateAndDelete(boolean ok) {
        return withUpdate(ok).withDelete(ok);
    }

}
