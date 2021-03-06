package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.Content;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.*;
import net.txconsole.web.resource.EventResource;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import net.txconsole.web.support.GUIEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/ui")
public class UIController extends AbstractUIController implements UI {

    private final StructureService structureService;
    private final ContributionService contributionService;
    private final TranslationMapService translationMapService;
    private final ExportService exportService;
    private final EventService eventService;
    private final ResourceService resourceService;
    private final GUIEventService guiEventService;
    private final SecurityUtils securityUtils;
    private final Function<Locale, Function<Event, EventResource>> eventResourceFunction =
            new Function<Locale, Function<Event, EventResource>>() {
                @Override
                public Function<Event, EventResource> apply(final Locale locale) {
                    return new Function<Event, EventResource>() {
                        @Override
                        public EventResource apply(Event event) {
                            return new EventResource(
                                    event,
                                    guiEventService.getResourceEvent(
                                            locale,
                                            event.getSignature().getAuthorName(),
                                            event.getSignature().getTimestamp(),
                                            event.getEventCode()
                                    ),
                                    guiEventService.getEventMessage(locale, event)
                            );
                        }
                    };
                }
            };
    private Function<Locale, Function<ProjectSummary, Resource<ProjectSummary>>> projectSummaryResourceFn = new Function<Locale, Function<ProjectSummary, Resource<ProjectSummary>>>() {
        @Override
        public Function<ProjectSummary, Resource<ProjectSummary>> apply(final Locale locale) {
            return new Function<ProjectSummary, Resource<ProjectSummary>>() {
                @Override
                public Resource<ProjectSummary> apply(ProjectSummary project) {
                    return resourceService.getProject(locale, project);
                }
            };
        }
    };
    private Function<Locale, Function<BranchSummary, Resource<BranchSummary>>> branchSummaryResourceFn = new Function<Locale, Function<BranchSummary, Resource<BranchSummary>>>() {
        @Override
        public Function<BranchSummary, Resource<BranchSummary>> apply(final Locale locale) {
            return new Function<BranchSummary, Resource<BranchSummary>>() {
                @Override
                public Resource<BranchSummary> apply(BranchSummary branch) {
                    return resourceService.getBranch(locale, branch);
                }
            };
        }
    };
    private Function<Locale, Function<ContributionSummary, Resource<ContributionSummary>>> contributionSummaryResourceFn = new Function<Locale, Function<ContributionSummary, Resource<ContributionSummary>>>() {
        @Override
        public Function<ContributionSummary, Resource<ContributionSummary>> apply(final Locale locale) {
            return new Function<ContributionSummary, Resource<ContributionSummary>>() {
                @Override
                public Resource<ContributionSummary> apply(ContributionSummary contribution) {
                    return resourceService.getContribution(locale, contribution);
                }
            };
        }
    };

    @Autowired
    public UIController(ErrorHandler errorHandler, Strings strings, StructureService structureService, ContributionService contributionService, TranslationMapService translationMapService, ExportService exportService, EventService eventService, ResourceService resourceService, GUIEventService guiEventService, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.structureService = structureService;
        this.contributionService = contributionService;
        this.translationMapService = translationMapService;
        this.exportService = exportService;
        this.eventService = eventService;
        this.resourceService = resourceService;
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
        return projectSummaryResourceFn.apply(locale).apply(structureService.getProject(id));
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

    @RequestMapping(value = "/branch/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Resource<ProjectSummary> deleteBranch(Locale locale, @PathVariable int id) {
        return projectSummaryResourceFn.apply(locale).apply(structureService.deleteBranch(id));
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
        return branchSummaryResourceFn.apply(locale).apply(structureService.getBranch(id));
    }

    /**
     * Downloads a branch map as Excel
     */
    @RequestMapping(value = "/branch/{id}/export", method = RequestMethod.GET)
    public void exportBranch(@PathVariable int id, HttpServletResponse response) throws IOException {
        // Branch & project
        BranchSummary branch = structureService.getBranch(id);
        ProjectSummary project = structureService.getProject(branch.getProjectId());
        // Gets the latest version
        TranslationMap map = translationMapService.map(id, null);
        // File name
        String file = String.format("%s-%s.xls", project.getName(), branch.getName());
        // Export of the data into an Excel file
        Content content = exportService.excel(map);
        // Prepares the response
        response.setContentType(content.getType());
        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file));
        response.setContentLength(content.getBytes().length);
        // Gets the output
        ServletOutputStream out = response.getOutputStream();
        // Writes to the output
        out.write(content.getBytes());
        out.flush();
        out.close();
        // No response (already handled)
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
    @RequestMapping(value = "/branch/{id}/contribution", method = RequestMethod.OPTIONS)
    public
    @ResponseBody
    Resource<ContributionSummary> newContribution(Locale locale, @PathVariable int id) {
        return contributionSummaryResourceFn.apply(locale).apply(contributionService.blankContribution(id));
    }

    /**
     * List of contributions for a branch
     */
    @RequestMapping(value = "/branch/{id}/contribution", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<ContributionSummary>> getContributionList(Locale locale, @PathVariable int id) {
        return Lists.transform(
                contributionService.getContributionList(id),
                contributionSummaryResourceFn.apply(locale)
        );
    }

    @Override
    @RequestMapping(value = "/branch/{id}/contribution", method = RequestMethod.POST)
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
    @RequestMapping(value = "/contribution/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<ContributionSummary> getContribution(Locale locale, @PathVariable int id) {
        return contributionSummaryResourceFn.apply(locale).apply(contributionService.getContribution(id));
    }

    @RequestMapping(value = "/contribution/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteContribution(@PathVariable int id) {
        return contributionService.deleteContribution(id);
    }

    @RequestMapping(value = "/contribution/{id}/details", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ContributionDetail> getContributionDetails(Locale locale, @PathVariable int id) {
        return contributionService.getContributionDetails(id);
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public
    @ResponseBody
    List<EventResource> getEvents(
            Locale locale,
            @RequestParam(required = false) EventEntity entity,
            @RequestParam(required = false, defaultValue = "0") int entityId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count
    ) {
        return Lists.transform(
                eventService.getEvents(entity, entityId, offset, count),
                eventResourceFunction.apply(locale)
        );
    }

}
