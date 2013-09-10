package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.model.BranchCreationForm;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.core.security.SecurityCategory;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.StructureService;
import net.txconsole.service.security.ProjectFunction;
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
                    // TODO List of branches for the project
                    .withUpdate(securityUtils.isGranted(SecurityCategory.PROJECT, o.getId(), ProjectFunction.UPDATE))
                    .withDelete(securityUtils.isGranted(SecurityCategory.PROJECT, o.getId(), ProjectFunction.DELETE));
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
                    .withUpdateAndDelete(securityUtils.isGranted(SecurityCategory.PROJECT, o.getProjectId(), ProjectFunction.UPDATE));
        }
    };

    @Autowired
    public UIController(ErrorHandler errorHandler, Strings strings, StructureService structureService, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.structureService = structureService;
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
     * Gets a branch by its ID
     */
    @Override
    @RequestMapping(value = "/branch/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<BranchSummary> getBranch(@PathVariable int id) {
        return branchSummaryResourceFn.apply(structureService.getBranch(id));
    }
}
