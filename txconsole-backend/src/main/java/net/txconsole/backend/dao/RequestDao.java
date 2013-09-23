package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.model.*;
import net.txconsole.core.support.SimpleMessage;

import java.util.List;
import java.util.Locale;

public interface RequestDao {

    int createRequest(int branchId, String version, JsonConfiguration txFileExchangeConfig);

    TRequest getById(int requestId);

    List<TRequest> findByBranch(int branchId, int offset, int count);

    List<Integer> findCreated();

    JsonConfiguration getTxFileExchangeConfiguration(int requestId);

    void saveDiff(int requestId, TranslationDiff diff);

    void setStatus(int requestId, RequestStatus status);

    void setStatus(int requestId, RequestStatus status, SimpleMessage message);

    void setToVersion(int requestId, String version);

    void delete(int id);

    TranslationDiff loadDiff(int id);

    TranslationDiffEntry getRequestEntryDetails(int entryId);

    int getBranchIdForRequestEntry(int entryId);

    TranslationDiffEntryValue addValue(int entryId, Locale locale, String value);

    void editValue(int entryValueId, String value);

    Integer findRequestEntryId(int requestId, String bundleName, String sectionName, String keyName);
}
