package net.txconsole.web.controller;

import net.txconsole.core.model.RequestControlledView;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.web.resource.ProjectDashboardResource;
import net.txconsole.core.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface UIRequest {

    Resource<RequestSummary> getRequest(Locale locale, int id);

    Resource<RequestControlledView> uploadRequest(Locale locale, int requestId, Collection<MultipartFile> files);

    List<ProjectDashboardResource> dashboard(Locale locale);

}
