package net.txconsole.web.controller;

import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.web.resource.Resource;

import java.util.Locale;

public interface UI {

    Resource<ProjectSummary> getProject(Locale locale, int id);

    Resource<BranchSummary> getBranch(Locale locale, int id);
}
