package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.Content;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.RequestStatus;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffEntry;

import java.util.List;

public interface RequestDao {

    int createRequest(int branchId, String version, JsonConfiguration txFileExchangeConfig);

    TRequest getById(int requestId);

    List<TRequest> findByBranch(int branchId, int offset, int count);

    List<Integer> findCreated();

    JsonConfiguration getTxFileExchangeConfiguration(int requestId);

    void saveDiff(int requestId, TranslationDiff diff);

    void saveRequestFile(int requestId, Content content);

    void setStatus(int requestId, RequestStatus status);

    void setToVersion(int requestId, String version);

    Content getRequestFile(int requestId);

    void delete(int id);

    TranslationDiff loadDiff(int id);

    TranslationDiffEntry getRequestEntryDetails(int entryId);
}
