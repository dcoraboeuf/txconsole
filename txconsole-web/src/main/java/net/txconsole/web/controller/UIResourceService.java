package net.txconsole.web.controller;

import com.google.common.base.Function;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.ResourceService;
import net.txconsole.web.support.GUIEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UIResourceService implements ResourceService {

    private final GUIEventService guiEventService;
    private final SecurityUtils securityUtils;
    /**
     * Gets the resource for a project
     */
    private final Function<Locale, Function<ProjectSummary, Resource<ProjectSummary>>> projectSummaryResourceFn =
            new Function<Locale, Function<ProjectSummary, Resource<ProjectSummary>>>() {
                @Override
                public Function<ProjectSummary, Resource<ProjectSummary>> apply(final Locale locale) {
                    return new Function<ProjectSummary, Resource<ProjectSummary>>() {
                        @Override
                        public Resource<ProjectSummary> apply(ProjectSummary o) {
                            return new Resource<>(o)
                                    .withLink(linkTo(methodOn(UIController.class).getProject(locale, o.getId())).withSelfRel())
                                    .withLink(linkTo(methodOn(GUIController.class).getProject(locale, o.getId())).withRel(Resource.REL_GUI))
                                            // List of branches
                                    .withLink(linkTo(methodOn(UIController.class).getProjectBranches(locale, o.getId())).withRel("branches"))
                                            // ACL
                                    .withActions(securityUtils, o.getId(), ProjectFunction.values())
                                            // Events
                                    .withEvent(guiEventService.getResourceEvent(locale, EventEntity.PROJECT, o.getId(), EventCode.PROJECT_CREATED));
                        }
                    };
                }
            };
    /**
     * Gets the resource for a branch
     */
    private final Function<Locale, Function<BranchSummary, Resource<BranchSummary>>> branchSummaryResourceFn =
            new Function<Locale, Function<BranchSummary, Resource<BranchSummary>>>() {
                @Override
                public Function<BranchSummary, Resource<BranchSummary>> apply(final Locale locale) {
                    return new Function<BranchSummary, Resource<BranchSummary>>() {
                        @Override
                        public Resource<BranchSummary> apply(BranchSummary o) {
                            return new Resource<>(o)
                                    .withLink(linkTo(methodOn(UIController.class).getBranch(locale, o.getId())).withSelfRel())
                                    .withLink(linkTo(methodOn(GUIController.class).getBranch(locale, o.getId())).withRel(Resource.REL_GUI))
                                            // Project link
                                    .withLink(linkTo(methodOn(UIController.class).getProject(locale, o.getProjectId())).withRel("project"))
                                            // ACL
                                    .withActions(securityUtils, o.getProjectId(), ProjectFunction.values())
                                            // Events
                                    .withEvent(guiEventService.getResourceEvent(locale, EventEntity.BRANCH, o.getId(), EventCode.BRANCH_CREATED));
                        }
                    };
                }
            };
    private final Function<Locale, Function<ContributionSummary, Resource<ContributionSummary>>> contributionSummaryResourceFn =
            new Function<Locale, Function<ContributionSummary, Resource<ContributionSummary>>>() {
                @Override
                public Function<ContributionSummary, Resource<ContributionSummary>> apply(final Locale locale) {
                    return new Function<ContributionSummary, Resource<ContributionSummary>>() {
                        @Override
                        public Resource<ContributionSummary> apply(ContributionSummary o) {
                            return new Resource<>(o)
                                    .withLink(linkTo(methodOn(GUIController.class).getContribution(locale, o.getId())).withRel(Resource.REL_GUI))
                                    .withLink(linkTo(methodOn(UIController.class).getContribution(locale, o.getId())).withRel(Resource.REL_GUI))
                                    .withEvent(guiEventService.getResourceEvent(locale, o.getAuthor().getFullName(), o.getTimestamp(), EventCode.CONTRIBUTION_CREATED));
                        }
                    };
                }
            };

    @Autowired
    public UIResourceService(GUIEventService guiEventService, SecurityUtils securityUtils) {
        this.guiEventService = guiEventService;
        this.securityUtils = securityUtils;
    }

    @Override
    public Resource<BranchSummary> getBranch(Locale locale, BranchSummary branch) {
        return branchSummaryResourceFn.apply(locale).apply(branch);
    }

    @Override
    public Resource<ProjectSummary> getProject(Locale locale, ProjectSummary project) {
        return projectSummaryResourceFn.apply(locale).apply(project);
    }

    @Override
    public Resource<ContributionSummary> getContribution(Locale locale, ContributionSummary contribution) {
        return contributionSummaryResourceFn.apply(locale).apply(contribution);
    }
}
