package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.core.model.PipelineCreationForm;
import net.txconsole.core.model.PipelineSummary;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.StructureService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import net.sf.jstring.Strings;
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
    private final SecurityUtils securityUtils;
    private final Function<PipelineSummary, Resource<PipelineSummary>> pipelineSummaryResourceFn = new Function<PipelineSummary, Resource<PipelineSummary>>() {
        @Override
        public Resource<PipelineSummary> apply(PipelineSummary o) {
            return new Resource<>(o)
                    .withLink(linkTo(methodOn(UIController.class).pipelineGet(o.getId())).withSelfRel())
                    .withLink(linkTo(methodOn(GUIController.class).pipelineGet(o.getName())).withRel(Resource.REL_GUI));
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
                .withLink(linkTo(methodOn(UIController.class).pipelineList()).withRel("pipelineList"));
    }

    @RequestMapping(value = "/pipeline", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<PipelineSummary>> pipelineList() {
        return Lists.transform(
                structureService.getPipelines(),
                pipelineSummaryResourceFn
        );
    }

    @RequestMapping(value = "/pipeline", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<PipelineSummary> pipelineCreate(@RequestBody PipelineCreationForm form) {
        return pipelineSummaryResourceFn.apply(structureService.createPipeline(form));
    }

    @RequestMapping(value = "/pipeline/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<PipelineSummary> pipelineGet(@PathVariable int id) {
        return pipelineSummaryResourceFn.apply(structureService.getPipeline(id));
    }
}
