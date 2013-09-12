package net.txconsole.service;

import net.txconsole.core.model.RequestConfigurationData;
import net.txconsole.core.model.RequestCreationForm;
import net.txconsole.core.model.RequestSummary;

public interface RequestService {

    RequestConfigurationData getRequestConfigurationData(int branchId);

    RequestSummary createRequest(int branchId, RequestCreationForm form);
}
