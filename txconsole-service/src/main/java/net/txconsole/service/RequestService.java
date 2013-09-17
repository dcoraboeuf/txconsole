package net.txconsole.service;

import net.txconsole.core.Content;
import net.txconsole.core.model.RequestConfigurationData;
import net.txconsole.core.model.RequestCreationForm;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.core.model.RequestView;

import java.util.List;

public interface RequestService {

    RequestConfigurationData getRequestConfigurationData(int branchId);

    RequestSummary createRequest(int branchId, RequestCreationForm form);

    RequestSummary getRequest(int requestId);

    List<RequestSummary> getRequestsForBranch(int branchId, int offset, int count);

    void launchCreation(int requestId);

    Content getRequestFile(int requestId);

    RequestSummary deleteRequest(int id);

    RequestView getRequestView(int id);
}
