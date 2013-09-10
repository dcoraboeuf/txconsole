package net.txconsole.web.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.txconsole.core.security.SecurityFunction;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class Resource<T> extends ResourceSupport {

    public static final String REL_GUI = "gui";

    private final T data;
    @JsonSerialize(using = SecurityFunctionSetSerializer.class)
    private final Set<SecurityFunction> actions = new HashSet<>();

    public Resource<T> withLink(Link link) {
        add(link);
        return this;
    }

    public Resource<T> withAction(SecurityFunction action) {
        actions.add(action);
        return this;
    }

    public Resource<T> withAction(SecurityFunction action, boolean ok) {
        if (ok) {
            return withAction(action);
        } else {
            return this;
        }
    }

}
