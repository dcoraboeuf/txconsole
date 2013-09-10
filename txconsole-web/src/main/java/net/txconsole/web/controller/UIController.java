package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.RequestService;
import net.txconsole.service.StructureService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/ui")
public class UIController extends AbstractUIController implements UI {

    private final StructureService structureService;
    private final RequestService requestService;
    private final SecurityUtils securityUtils;
    /**
     * Gets the resource for a project
     */
    private final Function<ProjectSummary, Resource<ProjectSummary>> projectSummaryResourceFn = new Function<ProjectSummary, Resource<ProjectSummary>>() {
        @Override
        public Resource<ProjectSummary> apply(ProjectSummary o) {
            return new Resource<>(o)
                    .withLink(linkTo(methodOn(UIController.class).getProject(o.getId())).withSelfRel())
                    .withLink(linkTo(methodOn(GUIController.class).getProject(o.getId())).withRel(Resource.REL_GUI))
                            // List of branches
                    .withLink(linkTo(methodOn(UIController.class).getProjectBranches(o.getId())).withRel("branches"))
                            // ACL
                    .withAction(ProjectFunction.UPDATE, securityUtils.isGranted(ProjectFunction.UPDATE, o.getId()))
                    .withAction(ProjectFunction.DELETE, securityUtils.isGranted(ProjectFunction.DELETE, o.getId()))
                    .withAction(ProjectFunction.REQUEST_CREATE, securityUtils.isGranted(ProjectFunction.REQUEST_CREATE, o.getId()));
        }
    };
    /**
     * Gets the resource for a branch
     */
    private final Function<BranchSummary, Resource<BranchSummary>> branchSummaryResourceFn = new Function<BranchSummary, Resource<BranchSummary>>() {
        @Override
        public Resource<BranchSummary> apply(BranchSummary o) {
            return new Resource<>(o)
                    .withLink(linkTo(methodOn(UIController.class).getBranch(o.getId())).withSelfRel())
                    .withLink(linkTo(methodOn(GUIController.class).getBranch(o.getId())).withRel(Resource.REL_GUI))
                            // Project link
                    .withLink(linkTo(methodOn(UIController.class).getProject(o.getProjectId())).withRel("project"))
                            // ACL
                    .withAction(ProjectFunction.UPDATE, securityUtils.isGranted(ProjectFunction.UPDATE, o.getProjectId()))
                    .withAction(ProjectFunction.DELETE, securityUtils.isGranted(ProjectFunction.DELETE, o.getProjectId()))
                    .withAction(ProjectFunction.REQUEST_CREATE, securityUtils.isGranted(ProjectFunction.REQUEST_CREATE, o.getProjectId()));
        }
    };
    /**
     * Gets the resource for a request configuration data
     */
    private final Function<RequestConfigurationData, Resource<RequestConfigurationData>> requestConfigurationDataResourceFn =
            new Function<RequestConfigurationData, Resource<RequestConfigurationData>>() {
                @Override
                public Resource<RequestConfigurationData> apply(RequestConfigurationData o) {
                    return new Resource<>(o)
                            .withLink(linkTo(methodOn(UIController.class).getBranch(o.getBranch().getId())).withRel("branch"))
                            .withLink(linkTo(methodOn(GUIController.class).getBranch(o.getBranch().getId())).withRel("branch-gui"))
                            .withLink(linkTo(methodOn(UIController.class).getProject(o.getProject().getId())).withRel("project"))
                            .withLink(linkTo(methodOn(GUIController.class).getProject(o.getProject().getId())).withRel("project-gui"));
                }
            };

    @Autowired
    public UIController(ErrorHandler errorHandler, Strings strings, StructureService structureService, RequestService requestService, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.structureService = structureService;
        this.requestService = requestService;
        this.securityUtils = securityUtils;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<String> home() {
        return new Resource<>("home")
                .withLink(linkTo(methodOn(UIController.class).home()).withSelfRel())
                .withLink(linkTo(methodOn(GUIController.class).home()).withRel(Resource.REL_GUI))
                .withLink(linkTo(methodOn(UIController.class).getProjectList()).withRel("projectList"));
    }

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<ProjectSummary>> getProjectList() {
        return Lists.transform(
                structureService.getProjects(),
                projectSummaryResourceFn
        );
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<ProjectSummary> createProject(@RequestBody ProjectCreationForm form) {
        return projectSummaryResourceFn.apply(structureService.createProject(form));
    }

    @Override
    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<ProjectSummary> getProject(@PathVariable int id) {
        return projectSummaryResourceFn.apply(structureService.getProject(id));
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
    Resource<String> deleteProject(@PathVariable int id) {
        structureService.deleteProject(id);
        return home();
    }

    /**
     * Creates a branch for a project
     */
    @RequestMapping(value = "/project/{id}/branch", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<BranchSummary> createBranch(@PathVariable int id, @RequestBody BranchCreationForm form) {
        return branchSummaryResourceFn.apply(structureService.createBranch(id, form));
    }

    /**
     * List of branches for a project
     */
    @RequestMapping(value = "/project/{id}/branch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<BranchSummary>> getProjectBranches(@PathVariable int id) {
        return Lists.transform(
                structureService.getProjectBranches(id),
                branchSummaryResourceFn
        );
    }

    /**
     * Gets a branch by its ID
     */
    @Override
    @RequestMapping(value = "/branch/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<BranchSummary> getBranch(@PathVariable int id) {
        return branchSummaryResourceFn.apply(structureService.getBranch(id));
    }

    /**
     * Gets the configuration data to create a translation request for a branch.
     *
     * @param branchId ID of the branch
     * @return Configuration data
     */
    @Override
    @RequestMapping(value = "/branch/{branchId}/request", method = RequestMethod.GET)
    public Resource<RequestConfigurationData> getRequestConfigurationData(@PathVariable int branchId) {
        return requestConfigurationDataResourceFn.apply(
                requestService.getRequestConfigurationData(branchId)
        );
    }
}
