package net.txconsole.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventForm {

    private final EventCode code;
    private final Collection<String> parameters;
    private final Map<String, Integer> entities;

    private EventForm(EventCode code,
                      Map<String, Integer> entities,
                      String... parameters) {
        this(code, Arrays.asList(parameters), entities);
    }

    public static EventForm projectCreated(ProjectSummary project) {
        return new EventForm(
                EventCode.PROJECT_CREATED,
                Collections.singletonMap("PROJECT", project.getId()),
                project.getName()
        );
    }
}
