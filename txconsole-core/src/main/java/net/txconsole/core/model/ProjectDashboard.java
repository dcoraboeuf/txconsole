package net.txconsole.core.model;

import lombok.Data;

import java.util.List;

/**
 * Summary of branches & current requests for a project.
 */
@Data
public class ProjectDashboard {

    private final ProjectSummary project;
    private final List<BranchDashboard> branches;

}
