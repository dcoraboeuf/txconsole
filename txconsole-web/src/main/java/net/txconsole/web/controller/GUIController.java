package net.txconsole.web.controller;

import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.support.MapBuilder;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.AbstractGUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
public class GUIController extends AbstractGUIController {

    private final UI ui;

    @Autowired
    public GUIController(ErrorHandler errorHandler, UI ui) {
        super(errorHandler);
        this.ui = ui;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("home");
    }

    /**
     * Project page
     */
    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    public ModelAndView getProject(Locale locale, @PathVariable int id) {
        return new ModelAndView("project", "project", ui.getProject(locale, id));
    }

    /**
     * Branch page
     */
    @RequestMapping(value = "/branch/{id}", method = RequestMethod.GET)
    public ModelAndView getBranch(Locale locale, @PathVariable int id) {
        Resource<BranchSummary> branch = ui.getBranch(locale, id);
        return new ModelAndView("branch",
                MapBuilder
                        .params()
                        .with("branch", branch)
                        .with("project", ui.getProject(locale, branch.getData().getProjectId()))
                        .get()
        );
    }

}
