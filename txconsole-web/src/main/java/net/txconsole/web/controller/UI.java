package net.txconsole.web.controller;

import net.txconsole.core.model.ProjectSummary;
import net.txconsole.web.resource.Resource;

public interface UI {

    Resource<ProjectSummary> projectGet(int id);

}
