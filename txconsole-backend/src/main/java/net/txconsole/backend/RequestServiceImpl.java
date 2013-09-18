package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.backend.exceptions.TranslationDiffEntryNotEditableException;
import net.txconsole.backend.exceptions.TranslationDiffEntryValueNotEditableException;
import net.txconsole.core.Content;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.EventService;
import net.txconsole.service.RequestService;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import net.txconsole.service.support.TxFileExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class RequestServiceImpl implements RequestService {

    private final Logger logger = LoggerFactory.getLogger(RequestService.class);
    private final StructureService structureService;
    private final TranslationMapService translationMapService;
    private final TranslationSourceService translationSourceService;
    private final EventService eventService;
    private final RequestDao requestDao;
    private final SecurityUtils securityUtils;
    /**
     * Requests being generated
     */
    private final Set<Integer> inGenerationRequests = new ConcurrentSkipListSet<>();
    /**
     * Request summary
     */
    private final Function<TRequest, RequestSummary> requestSummaryFn = new Function<TRequest, RequestSummary>() {
        @Override
        public RequestSummary apply(TRequest t) {
            RequestStatus status = t.getStatus();
            if (status == RequestStatus.CREATED && inGenerationRequests.contains(t.getId())) {
                status = RequestStatus.REQUEST_GENERATION;
            }
            return new RequestSummary(
                    t.getId(),
                    t.getBranchId(),
                    t.getVersion(),
                    status
            );
        }
    };

    @Autowired
    public RequestServiceImpl(StructureService structureService, TranslationMapService translationMapService, TranslationSourceService translationSourceService, EventService eventService, RequestDao requestDao, SecurityUtils securityUtils) {
        this.structureService = structureService;
        this.translationMapService = translationMapService;
        this.translationSourceService = translationSourceService;
        this.eventService = eventService;
        this.requestDao = requestDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestConfigurationData getRequestConfigurationData(int branchId) {
        // Loading the structure information
        BranchSummary branchSummary = structureService.getBranch(branchId);
        ProjectSummary projectSummary = structureService.getProject(branchSummary.getProjectId());
        // Gets the configuration information
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
        Object configuration = configuredTranslationSource.getConfiguration();
        TranslationSource<Object> translationSource = configuredTranslationSource.getConfigurable();
        // Last version
        // TODO Last version can be extracted from the list of past requests
        String lastVersion = "";
        // OK
        return new RequestConfigurationData(
                projectSummary,
                branchSummary,
                translationSource.getVersionSemantics(configuration),
                lastVersion
        );
    }

    @Override
    @Transactional
    public RequestSummary createRequest(int branchId, RequestCreationForm form) {
        // Loads the branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks the rights
        securityUtils.checkGrant(ProjectFunction.REQUEST_CREATE, branch.getProjectId());
        // Saves the request
        int requestId = requestDao.createRequest(branchId, form.getVersion(), form.getTxFileExchangeConfig());
        // Gets the request summary
        RequestSummary requestSummary = getRequest(requestId);
        // Event creation
        eventService.event(EventForm.requestCreated(
                requestSummary,
                branch,
                structureService.getProject(branch.getProjectId())
        ));
        // The request treatment is launched asynchronously (see )
        // Returns the request summary
        return requestSummary;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestSummary getRequest(int requestId) {
        return requestSummaryFn.apply(requestDao.getById(requestId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestSummary> getRequestsForBranch(int branchId, int offset, int count) {
        return Lists.transform(
                requestDao.findByBranch(branchId, offset, count),
                requestSummaryFn
        );
    }

    @Override
    @Transactional
    public void launchCreation(int requestId) {
        // Admin account? (granted by the RequestCreationBatch class)
        securityUtils.checkIsAdmin();
        // Gets the request
        TRequest request = requestDao.getById(requestId);
        // Checks the status
        if (request.getStatus() != RequestStatus.CREATED) {
            logger.warn("[request] The request {} is no longer in {} status", requestId, RequestStatus.CREATED);
        } else {
            inGenerationRequests.add(requestId);
            try {
                int branchId = request.getBranchId();
                String version = request.getVersion();
                // Gets the configuration for the branch
                Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
                // Default locale
                Locale defaultLocale = configuredTranslationSource.getConfigurable().getDefaultLocale(configuredTranslationSource.getConfiguration());
                // Gets the translation map for the given version
                TranslationMap oldMap = translationMapService.map(branchId, version);
                // Gets the translation map for the last version
                TranslationMap newMap = translationMapService.map(branchId, null);
                // Gets the diff between the two maps
                TranslationDiff diff = translationMapService.diff(defaultLocale, oldMap, newMap);
                // Saves the diff into the database
                requestDao.saveDiff(requestId, diff);
                // Changes the status to 'EXPORTED'
                requestDao.setStatus(requestId, RequestStatus.REQUEST_EXPORTED);
                // Saves the last version with the new status
                requestDao.setToVersion(requestId, newMap.getVersion());
                // TODO In case of error, sets the request status as 'ERROR', and resends the error
            } finally {
                inGenerationRequests.remove(requestId);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Content getRequestFile(int requestId) {
        // Gets the request
        TRequest request = requestDao.getById(requestId);
        int branchId = request.getBranchId();
        // Loads the diff
        TranslationDiff diff = requestDao.loadDiff(requestId);
        // Gets the configuration for the branch
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
        // Default locale & supported locales
        Locale defaultLocale = configuredTranslationSource.getConfigurable().getDefaultLocale(configuredTranslationSource.getConfiguration());
        Set<Locale> supportedLocales = configuredTranslationSource.getConfigurable().getSupportedLocales(configuredTranslationSource.getConfiguration());
        // Gets the configuration for the file exchange
        JsonConfiguration jsonConfiguration = requestDao.getTxFileExchangeConfiguration(requestId);
        // Gets the configured TxFileExchange from the JSON configuration
        Configured<Object, TxFileExchange<Object>> configuredTxFileExchange = translationSourceService.getConfiguredTxFileExchange(jsonConfiguration);
        // Export the diff as a file
        return configuredTxFileExchange.getConfigurable().export(
                configuredTxFileExchange.getConfiguration(),
                defaultLocale,
                supportedLocales,
                diff.forEdition(supportedLocales));
    }

    @Override
    @Transactional
    public RequestSummary deleteRequest(int id) {
        // Loads the request
        RequestSummary request = getRequest(id);
        // Loads the branch
        BranchSummary branch = structureService.getBranch(request.getBranchId());
        // Checks the rights
        securityUtils.checkGrant(ProjectFunction.REQUEST_DELETE, branch.getProjectId());
        // Deletion
        requestDao.delete(id);
        // OK
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestView getRequestView(int id) {
        // Gets the request summary
        RequestSummary summary = getRequest(id);
        // Gets the branch configuration
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(summary.getBranchId());
        // Gets the list of supported locales
        Set<Locale> supportedLocales = configuredTranslationSource.getConfigurable().getSupportedLocales(configuredTranslationSource.getConfiguration());
        // Loads the diff for this request
        TranslationDiff diff = requestDao.loadDiff(id).forEdition(supportedLocales).trimValues().sorted();
        // OK
        return new RequestView(summary, diff);
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDiffEntry getRequestEntryDetails(int entryId) {
        // Gets the branch ID from the entry ID
        int branchId = requestDao.getBranchIdForRequestEntry(entryId);
        // Gets the branch configuration
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
        // Gets the list of supported locales
        Set<Locale> supportedLocales = configuredTranslationSource.getConfigurable().getSupportedLocales(configuredTranslationSource.getConfiguration());
        // Gets the details for edition
        TranslationDiffEntry rawEntry = requestDao.getRequestEntryDetails(entryId);
        TranslationDiffEntry entry = rawEntry.forEdition(supportedLocales);
        return entry != null ? entry : rawEntry;
    }

    @Override
    @Transactional
    public TranslationDiffEntryValue editRequestEntry(int entryId, RequestEntryInput input) {
        // Loads the branch for this entry
        BranchSummary branch = structureService.getBranch(requestDao.getBranchIdForRequestEntry(entryId));
        // Checks the rights
        securityUtils.checkGrant(ProjectFunction.REQUEST_EDIT, branch.getProjectId());
        // Gets the details for this entry
        TranslationDiffEntry entry = getRequestEntryDetails(entryId);
        // If not editable, rejects the changes
        if (!entry.isEditable()) {
            throw new TranslationDiffEntryNotEditableException(entry.getKey());
        }
        // Gets the entry value for the locale
        TranslationDiffEntryValue entryValue = entry.getEntryValue(input.getLocale());
        // Not existing
        if (entryValue == null) {
            // Creates the entry
            return requestDao.addValue(entryId, input.getLocale(), input.getValue());
        } else if (!entryValue.isEditable()) {
            // If not editable, rejects the changes
            throw new TranslationDiffEntryValueNotEditableException(entry.getKey(), input.getLocale());
        } else {
            // Edits it
            requestDao.editValue(entryValue.getEntryValueId(), input.getValue());
            // OK
            return entryValue;
        }
    }
}
