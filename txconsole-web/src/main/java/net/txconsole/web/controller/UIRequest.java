package net.txconsole.web.controller;

import net.txconsole.core.model.RequestSummary;
import net.txconsole.web.resource.Resource;

import java.util.Locale;

public interface UIRequest {

    Resource<RequestSummary> getRequest(Locale locale, int id);

}
