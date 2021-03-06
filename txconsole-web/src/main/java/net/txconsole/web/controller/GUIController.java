package net.txconsole.web.controller;

import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ContributionSummary;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.core.model.Resource;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.core.support.MapBuilder;
import net.txconsole.web.support.AbstractGUIController;
import net.txconsole.web.support.ErrorHandler;
import net.txconsole.web.support.ErrorHandlingMultipartResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Locale;

@Controller
public class GUIController extends AbstractGUIController {

    private final UI ui;
    private final UIRequest uiRequest;
    private final ErrorHandlingMultipartResolver errorHandlingMultipartResolver;
    private final SecurityUtils securityUtils;

    @Autowired
    public GUIController(ErrorHandler errorHandler, UI ui, UIRequest uiRequest, ErrorHandlingMultipartResolver errorHandlingMultipartResolver, SecurityUtils securityUtils) {
        super(errorHandler);
        this.ui = ui;
        this.uiRequest = uiRequest;
        this.errorHandlingMultipartResolver = errorHandlingMultipartResolver;
        this.securityUtils = securityUtils;
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
     * Project ACL page
     */
    @RequestMapping(value = "/project/{id}/acl", method = RequestMethod.GET)
    public ModelAndView getProjectACL(Locale locale, @PathVariable int id) {
        securityUtils.checkGrant(ProjectFunction.ACL, id);
        return new ModelAndView("project-acl", "project", ui.getProject(locale, id));
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

    /**
     * Request page
     */
    @RequestMapping(value = "/request/{id}", method = RequestMethod.GET)
    public ModelAndView getRequest(Locale locale, @PathVariable int id) {
        Resource<RequestSummary> request = uiRequest.getRequest(locale, id);
        Resource<BranchSummary> branch = ui.getBranch(locale, request.getData().getBranchId());
        return new ModelAndView("request",
                MapBuilder
                        .params()
                        .with("request", request)
                        .with("branch", branch)
                        .with("project", ui.getProject(locale, branch.getData().getProjectId()))
                        .get()
        );
    }

    /**
     * Uploads a response file to the request
     */
    @RequestMapping(value = "/request/{id}/upload", method = RequestMethod.POST)
    public RedirectView uploadRequest(HttpServletRequest request, Locale locale, @PathVariable int id) {
        // Error handling
        errorHandlingMultipartResolver.checkForUploadError(request);
        // Gets the files
        Collection<MultipartFile> files = ((MultipartHttpServletRequest) request).getFileMap().values();
        // Upload
        uiRequest.uploadRequest(locale, id, files);
        // Success, redirect to the request page
        // TODO Includes the upload feedback as redirection attributes
        return new RedirectView("/request/" + id, true);
    }

    /**
     * Branch new contribution
     */
    @RequestMapping(value = "/branch/{id}/contribution/new", method = RequestMethod.GET)
    public ModelAndView getBranchNewContribution(Locale locale, @PathVariable int id) {
        // Gets the branch
        Resource<BranchSummary> branch = ui.getBranch(locale, id);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getData().getProjectId());
        // Returns the page
        return new ModelAndView("contribution",
                MapBuilder
                        .params()
                                // Branch & project
                        .with("branch", branch)
                        .with("project", ui.getProject(locale, branch.getData().getProjectId()))
                                // Empty contribution
                        .with("contribution", ui.newContribution(locale, id))
                                // OK
                        .get()
        );
    }

    /**
     * List of contributions
     */
    @RequestMapping(value = "/branch/{branch}/contribution", method = RequestMethod.GET)
    public ModelAndView getContributions(Locale locale, @PathVariable int branch) {
        Resource<BranchSummary> branchSummary = ui.getBranch(locale, branch);
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION_REVIEW, branchSummary.getData().getProjectId());
        return new ModelAndView("contributions",
                MapBuilder
                        .params()
                        .with("branch", branchSummary)
                        .with("project", ui.getProject(locale, branchSummary.getData().getProjectId()))
                        .get()
        );
    }

    @RequestMapping(value = "/contribution/{id}", method = RequestMethod.GET)
    public ModelAndView getContribution(Locale locale, @PathVariable int id) {
        Resource<ContributionSummary> contribution = ui.getContribution(locale, id);
        Resource<BranchSummary> branch = ui.getBranch(locale, contribution.getData().getBranch());
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION_REVIEW, branch.getData().getProjectId());
        return new ModelAndView("contribution",
                MapBuilder
                        .params()
                        .with("contribution", contribution)
                        .with("branch", branch)
                        .with("project", ui.getProject(locale, branch.getData().getProjectId()))
                        .get()
        );
    }
}
