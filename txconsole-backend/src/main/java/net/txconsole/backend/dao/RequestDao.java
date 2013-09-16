package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.model.JsonConfiguration;

import java.util.List;

public interface RequestDao {

    int createRequest(int branchId, String version, JsonConfiguration txFileExchangeConfig);

    TRequest getById(int requestId);

    List<TRequest> findByBranch(int branchId, int offset, int count);

    List<Integer> findCreated();

    JsonConfiguration getTxFileExchangeConfiguration(int requestId);
}
