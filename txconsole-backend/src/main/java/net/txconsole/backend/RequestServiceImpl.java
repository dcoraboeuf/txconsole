package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
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
                // Gets the configuration for the file exchange
                JsonConfiguration jsonConfiguration = requestDao.getTxFileExchangeConfiguration(requestId);
                // Gets the configured TxFileExchange from the JSON configuration
                Configured<Object, TxFileExchange<Object>> configuredTxFileExchange = translationSourceService.getConfiguredTxFileExchange(jsonConfiguration);
                // Gets the translation map for the given version
                TranslationMap oldMap = translationMapService.map(branchId, version);
                // Gets the translation map for the last version
                // TODO Gets the last version from this translation map
                TranslationMap newMap = translationMapService.map(branchId, null);
                // Gets the diff between the two maps
                TranslationDiff diff = translationMapService.diff(defaultLocale, oldMap, newMap);
                // FIXME Saves the diff into the database (takes way too much time...)
                // requestDao.saveDiff(requestId, diff);
                // Export the diff as a file
                Content content = configuredTxFileExchange.getConfigurable().export(
                        configuredTxFileExchange.getConfiguration(),
                        defaultLocale,
                        configuredTranslationSource.getConfigurable().getSupportedLocales(configuredTranslationSource.getConfiguration()),
                        diff);
                // Saves the diff file into the database
                requestDao.saveRequestFile(requestId, content);
                // Changes the status to 'EXPORTED'
                requestDao.setStatus(requestId, RequestStatus.REQUEST_EXPORTED);
                // TODO Saves the last version with the new status
                // TODO In case of error, sets the request status as 'ERROR', and resends the error
            } finally {
                inGenerationRequests.remove(requestId);
            }
        }
    }
}
