package net.txconsole.web.controller;

import net.sf.jstring.Strings;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ContributionForm;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.model.ContributionResult;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.ContributionService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
public class UIContributionController extends AbstractUIController implements UIContribution {

    private final UI ui;
    private final ContributionService contributionService;
    private final SecurityUtils securityUtils;

    @Autowired
    public UIContributionController(ErrorHandler errorHandler, Strings strings, UI ui, ContributionService contributionService, SecurityUtils securityUtils, Strings strings1) {
        super(errorHandler, strings);
        this.ui = ui;
        this.contributionService = contributionService;
        this.securityUtils = securityUtils;
    }

    @Override
    @RequestMapping(value = "/ui/branch/{id}/contribution", method = RequestMethod.GET)
    public
    @ResponseBody
    ContributionForm newContribution(Locale locale, @PathVariable int id) {
        // Gets the branch
        Resource<BranchSummary> branch = ui.getBranch(locale, id);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getData().getProjectId());
        // Returns the page
        return new ContributionForm(securityUtils.isGranted(ProjectFunction.CONTRIBUTION_DIRECT, branch.getData().getProjectId()));
    }

    @Override
    @RequestMapping(value = "/ui/branch/{id}/contribution", method = RequestMethod.POST)
    public
    @ResponseBody
    ContributionResult postContribution(Locale locale, @PathVariable int id, @RequestBody ContributionInput input) {
        // Gets the branch
        Resource<BranchSummary> branch = ui.getBranch(locale, id);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getData().getProjectId());
        // OK
        return new ContributionResult(
                contributionService.post(id, input).getLocalizedMessage(strings, locale)
        );
    }
}
