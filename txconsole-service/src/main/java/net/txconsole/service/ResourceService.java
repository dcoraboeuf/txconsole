package net.txconsole.service;

import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.core.model.Resource;

import java.util.Locale;

public interface ResourceService {

    Resource<BranchSummary> getBranch(Locale locale, BranchSummary branch);

    Resource<ProjectSummary> getProject(Locale locale, ProjectSummary project);

}
