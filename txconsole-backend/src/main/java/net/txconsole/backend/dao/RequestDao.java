package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.Content;
import net.txconsole.core.model.*;

import java.util.List;
import java.util.Locale;

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

    int getBranchIdForRequestEntry(int entryId);

    TranslationDiffEntryValue addValue(int entryId, Locale locale, String value);

    void editValue(int entryValueId, String value);
}
