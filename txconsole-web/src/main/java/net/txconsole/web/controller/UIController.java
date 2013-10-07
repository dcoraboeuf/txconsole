package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.ContributionService;
import net.txconsole.service.ResourceService;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import net.txconsole.web.support.GUIEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/ui")
public class UIController extends AbstractUIController implements UI, ResourceService {

    private final StructureService structureService;
    private final ContributionService contributionService;
    private final TranslationMapService translationMapService;
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
    private final Function<Locale, Function<ContributionSummary, Resource<ContributionSummary>>> contributionSummaryFn =
            new Function<Locale, Function<ContributionSummary, Resource<ContributionSummary>>>() {
                @Override
                public Function<ContributionSummary, Resource<ContributionSummary>> apply(final Locale locale) {
                    return new Function<ContributionSummary, Resource<ContributionSummary>>() {
                        @Override
                        public Resource<ContributionSummary> apply(ContributionSummary o) {
                            return new Resource<>(o)
                                    .withLink(linkTo(methodOn(GUIController.class).getContribution(locale, o.getId())).withRel(Resource.REL_GUI))
                                    .withLink(linkTo(methodOn(UIController.class).getContribution(locale, o.getId())).withRel(Resource.REL_GUI));
                        }
                    };
                }
            };

    @Autowired
    public UIController(ErrorHandler errorHandler, Strings strings, StructureService structureService, ContributionService contributionService, TranslationMapService translationMapService, GUIEventService guiEventService, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.structureService = structureService;
        this.contributionService = contributionService;
        this.translationMapService = translationMapService;
        this.guiEventService = guiEventService;
        this.securityUtils = securityUtils;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<String> home(Locale locale) {
        return new Resource<>("home")
                .withLink(linkTo(methodOn(UIController.class).home(locale)).withSelfRel())
                .withLink(linkTo(methodOn(GUIController.class).home()).withRel(Resource.REL_GUI))
                .withLink(linkTo(methodOn(UIController.class).getProjectList(locale)).withRel("projectList"));
    }

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<ProjectSummary>> getProjectList(Locale locale) {
        return Lists.transform(
                structureService.getProjects(),
                projectSummaryResourceFn.apply(locale)
        );
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<ProjectSummary> createProject(Locale locale, @RequestBody ProjectCreationForm form) {
        return projectSummaryResourceFn.apply(locale).apply(structureService.createProject(form));
    }

    @Override
    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<ProjectSummary> getProject(Locale locale, @PathVariable int id) {
        return getProject(locale, structureService.getProject(id));
    }

    @Override
    public Resource<ProjectSummary> getProject(Locale locale, ProjectSummary project) {
        return projectSummaryResourceFn.apply(locale).apply(project);
    }

    /**
     * Gets the list of parameters for a project.
     *
     * @param id ID of the project
     * @return List of parameters
     */
    @RequestMapping(value = "/project/{id}/parameter", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getProjectParameters(@PathVariable int id) {
        return structureService.getProjectParameters(id);
    }

    @RequestMapping(value = "/project/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Resource<String> deleteProject(Locale locale, @PathVariable int id) {
        structureService.deleteProject(id);
        return home(locale);
    }

    /**
     * Creates a branch for a project
     */
    @RequestMapping(value = "/project/{id}/branch", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<BranchSummary> createBranch(Locale locale, @PathVariable int id, @RequestBody BranchCreationForm form) {
        return branchSummaryResourceFn.apply(locale).apply(structureService.createBranch(id, form));
    }

    /**
     * List of branches for a project
     */
    @RequestMapping(value = "/project/{id}/branch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<BranchSummary>> getProjectBranches(Locale locale, @PathVariable int id) {
        return Lists.transform(
                structureService.getProjectBranches(id),
                branchSummaryResourceFn.apply(locale)
        );
    }

    /**
     * Gets a branch by its ID
     */
    @Override
    @RequestMapping(value = "/branch/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<BranchSummary> getBranch(Locale locale, @PathVariable int id) {
        return getBranch(locale, structureService.getBranch(id));
    }

    @Override
    public Resource<BranchSummary> getBranch(Locale locale, BranchSummary branch) {
        return branchSummaryResourceFn.apply(locale).apply(branch);
    }

    /**
     * Gets the translation map for a branch, for a given version and a given filter.
     */
    @RequestMapping(value = "/map/{branchId}", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<TranslationMapResponse> getTranslationMap(Locale locale, @PathVariable int branchId, @RequestBody TranslationMapRequest request) {
        return new Resource<>(translationMapService.map(branchId, request.getVersion()).filter(request.getLimit(), request.getFilter()))
                .withLink(linkTo(methodOn(UIController.class).getBranch(locale, branchId)).withRel("branch"))
                .withLink(linkTo(methodOn(GUIController.class).getBranch(locale, branchId)).withRel("branch-gui"));
        // TODO ACL for the map edition
    }

    @Override
    @RequestMapping(value = "/ui/branch/{id}/contribution", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<ContributionSummary> newContribution(Locale locale, @PathVariable int id) {
        // OK
        return getContribution(locale, contributionService.blankContribution(id));
    }

    @Override
    @RequestMapping(value = "/ui/branch/{id}/contribution", method = RequestMethod.POST)
    public
    @ResponseBody
    ContributionResult postContribution(Locale locale, @PathVariable int id, @RequestBody ContributionInput input) {
        // Gets the branch
        Resource<BranchSummary> branch = getBranch(locale, id);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getData().getProjectId());
        // OK
        return new ContributionResult(
                contributionService.post(id, input).getLocalizedMessage(strings, locale)
        );
    }

    @Override
    @RequestMapping(value = "/ui/contribution/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<ContributionSummary> getContribution(Locale locale, @PathVariable int id) {
        return getContribution(locale, contributionService.getContribution(id));
    }

    @Override
    public Resource<ContributionSummary> getContribution(Locale locale, ContributionSummary contribution) {
        return contributionSummaryFn.apply(locale).apply(contribution);
    }
}
