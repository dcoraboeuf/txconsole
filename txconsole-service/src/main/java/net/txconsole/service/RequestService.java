package net.txconsole.service;

import net.txconsole.core.Content;
import net.txconsole.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface RequestService {

    String MISSING_LOCALE_IN_KEY = "RequestService.control.missingLocaleInKey";
    String UNCHANGED_LOCALE_IN_KEY = "RequestService.control.unchangedLocaleInKey";
    String GENERAL_ERROR = "RequestService.generalError";

    RequestConfigurationData getRequestConfigurationData(int branchId);

    RequestSummary createRequest(int branchId, RequestCreationForm form);

    RequestSummary getRequest(int requestId);

    List<RequestSummary> getRequestsForBranch(int branchId, int offset, int count);

    void launchCreation(int requestId);

    Content getRequestFile(int requestId);

    RequestSummary deleteRequest(int id);

    RequestView getRequestView(int id);

    TranslationDiffEntry getRequestEntryDetails(int entryId);

    RequestControlledEntryValue editRequestEntry(Locale outputLocale, int entryId, RequestEntryInput input);

    List<TranslationDiffControl> controlRequest(Locale outputLocale, int requestId);

    TranslationDiffControl controlRequestEntry(Locale outputLocale, int entryId);

    void uploadRequest(int id, Collection<MultipartFile> responses);

    void mergeRequest(int id, RequestMergeForm form);
}
