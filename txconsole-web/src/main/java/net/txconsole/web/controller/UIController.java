package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
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
public class UIController extends AbstractUIController {

    private final StructureService structureService;
    private final Function<ProjectSummary, Resource<ProjectSummary>> projectSummaryResourceFn = new Function<ProjectSummary, Resource<ProjectSummary>>() {
        @Override
        public Resource<ProjectSummary> apply(ProjectSummary o) {
            return new Resource<>(o)
                    .withLink(linkTo(methodOn(UIController.class).projectGet(o.getId())).withSelfRel())
                    .withLink(linkTo(methodOn(GUIController.class).projectGet(o.getId())).withRel(Resource.REL_GUI));
        }
    };

    @Autowired
    public UIController(ErrorHandler errorHandler, Strings strings, StructureService structureService) {
        super(errorHandler, strings);
        this.structureService = structureService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<String> home() {
        return new Resource<>("home")
                .withLink(linkTo(methodOn(UIController.class).home()).withSelfRel())
                .withLink(linkTo(methodOn(GUIController.class).home()).withRel(Resource.REL_GUI))
                .withLink(linkTo(methodOn(UIController.class).projectList()).withRel("projectList"));
    }

    @RequestMapping(value = "/project", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<ProjectSummary>> projectList() {
        return Lists.transform(
                structureService.getProjects(),
                projectSummaryResourceFn
        );
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<ProjectSummary> projectCreate(@RequestBody ProjectCreationForm form) {
        return projectSummaryResourceFn.apply(structureService.createProject(form));
    }

    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<ProjectSummary> projectGet(@PathVariable int id) {
        return projectSummaryResourceFn.apply(structureService.getProject(id));
    }
}
