package net.txconsole.service;

import net.txconsole.core.model.*;

import java.util.List;

public interface StructureService {

    List<ProjectSummary> getProjects();

    ProjectSummary getProject(int id);

    Ack deleteProject(int id);

    ProjectSummary createProject(ProjectCreationForm form);

    List<String> getProjectParameters(int id);

    BranchSummary getBranch(int id);

    BranchSummary createBranch(int project, BranchCreationForm form);
}
