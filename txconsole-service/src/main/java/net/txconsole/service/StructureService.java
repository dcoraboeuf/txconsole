package net.txconsole.service;

import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;

import java.util.List;

public interface StructureService {

    List<ProjectSummary> getProjects();

    ProjectSummary getProject(int id);

    ProjectSummary createProject(ProjectCreationForm form);
}
