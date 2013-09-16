package net.txconsole.service;

import net.txconsole.core.model.RequestConfigurationData;
import net.txconsole.core.model.RequestCreationForm;
import net.txconsole.core.model.RequestSummary;

import java.util.List;

public interface RequestService {

    RequestConfigurationData getRequestConfigurationData(int branchId);

    RequestSummary createRequest(int branchId, RequestCreationForm form);

    RequestSummary getRequest(int requestId);

    List<RequestSummary> getRequestsForBranch(int branchId, int offset, int count);

    void launchCreation(int requestId);
}
