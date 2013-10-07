package net.txconsole.web.controller;

import net.txconsole.core.model.*;

import java.util.Locale;

public interface UI {

    Resource<ProjectSummary> getProject(Locale locale, int id);

    Resource<BranchSummary> getBranch(Locale locale, int id);

    Resource<ContributionSummary> getContribution(Locale locale, int id);

    Resource<ContributionSummary> newContribution(Locale locale, int branch);

    ContributionResult postContribution(Locale locale, int branch, ContributionInput input);

}
