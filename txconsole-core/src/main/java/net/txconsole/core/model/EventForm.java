package net.txconsole.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.txconsole.core.support.MapBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.lang.String.valueOf;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventForm {

    private final EventCode code;
    private final Collection<String> parameters;
    private final Map<EventEntity, Integer> entities;

    private EventForm(EventCode code,
                      Map<EventEntity, Integer> entities,
                      String... parameters) {
        this(code, Arrays.asList(parameters), entities);
    }

    public static EventForm projectCreated(ProjectSummary project) {
        return new EventForm(
                EventCode.PROJECT_CREATED,
                Collections.singletonMap(EventEntity.PROJECT, project.getId()),
                project.getName()
        );
    }

    public static EventForm projectDeleted(ProjectSummary project) {
        return new EventForm(
                EventCode.PROJECT_DELETED,
                Collections.<EventEntity, Integer>emptyMap(),
                project.getName()
        );
    }

    public static EventForm branchCreated(BranchSummary branch, ProjectSummary project) {
        return new EventForm(
                EventCode.BRANCH_CREATED,
                MapBuilder
                        .of(EventEntity.BRANCH, branch.getId())
                        .with(EventEntity.PROJECT, project.getId())
                        .get(),
                branch.getName(),
                project.getName()
        );
    }

    public static EventForm branchDeleted(ProjectSummary project, BranchSummary branch) {
        return new EventForm(
                EventCode.BRANCH_DELETED,
                MapBuilder
                        .of(EventEntity.PROJECT, project.getId())
                        .get(),
                branch.getName(),
                project.getName()
        );
    }

    public static EventForm requestCreated(RequestSummary request, BranchSummary branch, ProjectSummary project) {
        return requestEvent(EventCode.REQUEST_CREATED, request, branch, project);
    }

    public static EventForm requestDeleted(RequestSummary request, BranchSummary branch, ProjectSummary project) {
        return new EventForm(
                EventCode.REQUEST_DELETED,
                MapBuilder
                        .of(EventEntity.BRANCH, branch.getId())
                        .with(EventEntity.PROJECT, project.getId())
                        .get(),
                valueOf(request.getId()),
                request.getVersion(),
                branch.getName(),
                project.getName()
        );
    }

    public static EventForm requestMerged(RequestSummary request, BranchSummary branch, ProjectSummary project) {
        return requestEvent(EventCode.REQUEST_MERGED, request, branch, project);
    }

    private static EventForm requestEvent(EventCode eventCode, RequestSummary request, BranchSummary branch, ProjectSummary project) {
        return new EventForm(
                eventCode,
                MapBuilder
                        .of(EventEntity.REQUEST, request.getId())
                        .with(EventEntity.BRANCH, branch.getId())
                        .with(EventEntity.PROJECT, project.getId())
                        .get(),
                valueOf(request.getId()),
                request.getVersion(),
                branch.getName(),
                project.getName()
        );
    }
}
