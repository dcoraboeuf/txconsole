package net.txconsole.service;

import net.txconsole.core.model.*;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;

import java.util.List;

public interface StructureService {

    List<ProjectSummary> getProjects();

    ProjectSummary getProject(int id);

    Ack deleteProject(int id);

    ProjectSummary createProject(ProjectCreationForm form);

    List<String> getProjectParameters(int id);

    BranchSummary getBranch(int id);

    BranchSummary createBranch(int project, BranchCreationForm form);

    List<BranchSummary> getProjectBranches(int id);

    <C> Configured<C, TranslationSource<C>> getConfiguredTranslationSource(int branchId);

    ProjectSummary deleteBranch(int id);
}
