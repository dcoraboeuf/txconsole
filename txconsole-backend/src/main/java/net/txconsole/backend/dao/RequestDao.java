package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.model.JsonConfiguration;

public interface RequestDao {

    int createRequest(int branchId, JsonConfiguration txFileExchangeConfig);

    TRequest getById(int requestId);
}
