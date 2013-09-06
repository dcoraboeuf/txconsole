package net.txconsole.web.controller;

import net.txconsole.service.StructureService;
import net.txconsole.web.support.AbstractGUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GUIController extends AbstractGUIController {

    private final StructureService structureService;

    @Autowired
    public GUIController(ErrorHandler errorHandler, StructureService structureService) {
        super(errorHandler);
        this.structureService = structureService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("home");
    }

    /**
     * Pipeline page
     */
    @RequestMapping(value = "/pipeline/{name:[a-zA-Z0-9_\\.]+}", method = RequestMethod.GET)
    public ModelAndView pipelineGet(@PathVariable String name) {
        return new ModelAndView("pipeline", "pipeline", structureService.getPipelineByName(name));
    }

}
