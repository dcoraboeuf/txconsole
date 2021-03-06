package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.LocalizableException;
import net.sf.jstring.Strings;
import net.sf.jstring.model.*;
import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.backend.dao.model.TRequestEntry;
import net.txconsole.backend.exceptions.*;
import net.txconsole.core.Content;
import net.txconsole.core.NamedContent;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.core.support.SimpleMessage;
import net.txconsole.service.*;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import net.txconsole.service.support.TxFileExchange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
    private final Strings strings;
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
                status = RequestStatus.GENERATION;
            }
            return new RequestSummary(
                    t.getId(),
                    t.getBranchId(),
                    t.getVersion(),
                    t.getToVersion(),
                    t.getMergeVersion(),
                    status,
                    t.getMessage()
            );
        }
    };

    @Autowired
    public RequestServiceImpl(StructureService structureService, TranslationMapService translationMapService, TranslationSourceService translationSourceService, EventService eventService, RequestDao requestDao, SecurityUtils securityUtils, Strings strings) {
        this.structureService = structureService;
        this.translationMapService = translationMapService;
        this.translationSourceService = translationSourceService;
        this.eventService = eventService;
        this.requestDao = requestDao;
        this.securityUtils = securityUtils;
        this.strings = strings;
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
        // Last version can be extracted from the list of past requests
        String lastVersion = requestDao.getLastVersion(branchId);
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
        // Launches the treatment
        launchCreation(requestId);
        // Returns the request summary
        return getRequest(requestId);
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

    protected void launchCreation(int requestId) {
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
                requestDao.setStatus(requestId, RequestStatus.EXPORTED);
                // Saves the last version with the new status
                requestDao.setToVersion(requestId, newMap.getVersion());
            } catch (Exception ex) {
                // Gets the message
                SimpleMessage message;
                if (ex instanceof LocalizableException) {
                    LocalizableException lex = (LocalizableException) ex;
                    message = new SimpleMessage(
                            lex.getCode(),
                            lex.getParameters()
                    );
                } else {
                    message = new SimpleMessage(RequestService.GENERAL_ERROR);
                }
                // Sets the status to error
                requestDao.setStatus(requestId, RequestStatus.ERROR, message);
                // Logs the error
                logger.error(String.format("[request] ID = %d", requestId), ex);
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
        Configured<Object, TxFileExchange<Object>> configuredTxFileExchange = getConfiguredTxFileExchange(requestId);
        // Export the diff as a file
        return configuredTxFileExchange.getConfigurable().export(
                configuredTxFileExchange.getConfiguration(),
                defaultLocale,
                supportedLocales,
                diff.forEdition(supportedLocales));
    }

    protected Configured<Object, TxFileExchange<Object>> getConfiguredTxFileExchange(int requestId) {
        JsonConfiguration jsonConfiguration = requestDao.getTxFileExchangeConfiguration(requestId);
        // Gets the configured TxFileExchange from the JSON configuration
        return translationSourceService.getConfiguredTxFileExchange(jsonConfiguration);
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
        // Event creation
        eventService.event(EventForm.requestDeleted(
                request,
                branch,
                structureService.getProject(branch.getProjectId())
        ));
        // OK
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestView getRequestView(int id) {
        // Gets the request summary
        RequestSummary summary = getRequest(id);
        // Gets the branch configuration
        Set<Locale> supportedLocales = getSupportedLocalesForBranch(summary.getBranchId());
        // Loads the diff for this request
        TranslationDiff diff = requestDao.loadDiff(id).forEdition(supportedLocales).trimValues().sorted();
        // OK
        return new RequestView(summary, supportedLocales, diff);
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDiffEntry getRequestEntryDetails(int entryId) {
        // Gets the branch ID from the entry ID
        int branchId = requestDao.getBranchIdForRequestEntry(entryId);
        Set<Locale> supportedLocales = getSupportedLocalesForBranch(branchId);
        // Gets the details for edition
        TranslationDiffEntry rawEntry = requestDao.getRequestEntryDetails(entryId);
        TranslationDiffEntry entry = rawEntry.forEdition(supportedLocales);
        return entry != null ? entry : rawEntry;
    }

    protected Set<Locale> getSupportedLocalesForBranch(int branchId) {
        // Gets the branch configuration
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
        // Gets the list of supported locales
        return configuredTranslationSource.getConfigurable().getSupportedLocales(configuredTranslationSource.getConfiguration());
    }

    @Override
    @Transactional
    public RequestControlledEntryValue editRequestEntry(Locale outputLocale, int entryId, RequestEntryInput input) {
        // Gets the request information
        TRequestEntry tRequestEntry = requestDao.getRequestEntry(entryId);
        TRequest tRequest = requestDao.getById(tRequestEntry.getRequest());
        // Loads the branch for this entry
        BranchSummary branch = structureService.getBranch(tRequest.getBranchId());
        // Checks the rights
        securityUtils.checkGrant(ProjectFunction.REQUEST_EDIT, branch.getProjectId());
        // Checks the status of the request itself
        if (tRequest.getStatus() != RequestStatus.EXPORTED) {
            throw new RequestCannotBeEditedException();
        }
        // Checks the type of entry
        if (tRequestEntry.getType() == TranslationDiffType.DELETED) {
            throw new DeletedRequestEntryCannotBeEditedException();
        }
        // Gets the details for this entry
        TranslationDiffEntry entry = getRequestEntryDetails(entryId);
        // Edits the entry
        TranslationDiffEntryValue resultingEntryValue = editRequestEntry(entry, input);
        // OK
        return new RequestControlledEntryValue(
                resultingEntryValue,
                controlRequestEntry(outputLocale, entryId).getMessages()
        );
    }

    protected TranslationDiffEntryValue editRequestEntry(TranslationDiffEntry entry, RequestEntryInput input) {
        // Gets the entry value for the locale
        TranslationDiffEntryValue entryValue = entry.getEntryValue(input.getLocale());
        // Resulting entry value
        TranslationDiffEntryValue resultingEntryValue;
        // Not existing
        if (entryValue == null) {
            // Creates the entry
            resultingEntryValue = requestDao.addValue(entry.getEntryId(), input.getLocale(), input.getValue());
        } else {
            int entryValueId = entryValue.getEntryValueId();
            if (entryValueId > 0) {
                // Edits it
                requestDao.editValue(entryValueId, input.getValue());
                // OK
                resultingEntryValue = entryValue;
            } else {
                // Creates the entry
                resultingEntryValue = requestDao.addValue(entry.getEntryId(), input.getLocale(), input.getValue());
            }
        }
        return resultingEntryValue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationDiffControl> controlRequest(Locale outputLocale, int requestId) {
        // Gets the request summary
        RequestSummary summary = getRequest(requestId);
        // Gets the branch configuration
        Set<Locale> supportedLocales = getSupportedLocalesForBranch(summary.getBranchId());
        // Result
        List<TranslationDiffControl> controls = new ArrayList<>();
        // Loads the diff
        TranslationDiff diff = requestDao.loadDiff(requestId);
        // Loops over the entries
        for (TranslationDiffEntry entry : diff.getEntries()) {
            controls.addAll(controlEntry(outputLocale, entry, supportedLocales));
        }
        // OK
        return controls;
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDiffControl controlRequestEntry(Locale outputLocale, int entryId) {
        // Entry & values
        TranslationDiffEntry entry = getRequestEntryDetails(entryId);
        // Branch from the entry
        int branchId = requestDao.getBranchIdForRequestEntry(entryId);
        // Gets the branch configuration
        Set<Locale> supportedLocales = getSupportedLocalesForBranch(branchId);
        // Controls
        List<TranslationDiffControl> controls = controlEntry(outputLocale, entry, supportedLocales);
        // OK
        if (controls.isEmpty()) {
            return new TranslationDiffControl(
                    entryId,
                    entry.getBundle(),
                    entry.getSection(),
                    entry.getKey()
            );
        } else {
            return controls.get(0);
        }
    }

    @Override
    @Transactional
    public void uploadRequest(int requestId, Collection<MultipartFile> responses) {
        // No need to do anything if not response file is sent
        if (responses == null || responses.isEmpty()) {
            return;
        }
        // Loads the request
        RequestSummary request = getRequest(requestId);
        int branchId = request.getBranchId();
        // Checks the status of the request (must be EXPORTED)
        if (request.getStatus() != RequestStatus.EXPORTED) {
            throw new RequestCannotUploadBecauseOfStatusException(request.getStatus(), RequestStatus.EXPORTED);
        }
        // Loads the branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks the rights
        securityUtils.checkGrant(ProjectFunction.REQUEST_UPLOAD, branch.getProjectId());
        // Gets the configuration for the request exchange
        Configured<Object, TxFileExchange<Object>> configuredTxFileExchange = getConfiguredTxFileExchange(requestId);
        // Default locale & support locales
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
        Locale defaultLocale = configuredTranslationSource.getConfigurable().getDefaultLocale(configuredTranslationSource.getConfiguration());
        Set<Locale> supportedLocales = configuredTranslationSource.getConfigurable().getSupportedLocales(configuredTranslationSource.getConfiguration());
        // Uploads each response file
        for (MultipartFile response : responses) {
            // Reads the map for this response
            TranslationMap map = readResponse(configuredTxFileExchange, defaultLocale, supportedLocales, response);
            // Writes the map in the stored diff
            BundleCollection bundleCollection = map.getBundleCollection();
            for (Bundle bundle : bundleCollection.getBundles()) {
                String bundleName = bundle.getName();
                for (BundleSection section : bundle.getSections()) {
                    String sectionName = section.getName();
                    for (BundleKey key : section.getKeys()) {
                        // Gets the corresponding entry in the diff
                        String keyName = key.getName();
                        Integer entryId = requestDao.findRequestEntryId(requestId, bundleName, sectionName, keyName);
                        if (entryId != null) {
                            // Gets the details for this entry
                            TranslationDiffEntry entry = getRequestEntryDetails(entryId);
                            for (Map.Entry<Locale, BundleValue> bundleValueEntry : key.getValues().entrySet()) {
                                Locale locale = bundleValueEntry.getKey();
                                BundleValue bundleValue = bundleValueEntry.getValue();
                                String value = bundleValue.getValue();
                                // Edits this entry
                                editRequestEntry(
                                        entry,
                                        new RequestEntryInput(
                                                locale,
                                                value
                                        )
                                );
                            }
                        } else {
                            // No entry found, this is an error
                            throw new TranslationDiffEntryNotFoundException(bundleName, sectionName, keyName);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public String mergeRequest(int requestId, RequestMergeForm form) {
        // Loads the request
        RequestSummary request = getRequest(requestId);
        int branchId = request.getBranchId();
        // Checks the status of the request (must be EXPORTED)
        if (request.getStatus() != RequestStatus.EXPORTED) {
            throw new RequestCannotMergeBecauseOfStatusException(request.getStatus(), RequestStatus.EXPORTED);
        }
        // Loads the branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks the rights
        securityUtils.checkGrant(ProjectFunction.REQUEST_MERGE, branch.getProjectId());
        // Default locale & support locales
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
        // Reads the latest version of the translation map
        TranslationMap map = configuredTranslationSource.getConfigurable().read(configuredTranslationSource.getConfiguration(), null);
        // Gets the stored diff for this request
        TranslationDiff diff = requestDao.loadDiff(requestId);
        // Applies the diff to the map
        map = map.applyDiff(diff);
        // Writes back the map to the store
        String version = configuredTranslationSource.getConfigurable().write(configuredTranslationSource.getConfiguration(), map, form.getMessage());
        // Sets the merge version
        requestDao.setMergeVersion(requestId, version);
        // Changes the status of the request
        requestDao.setStatus(requestId, RequestStatus.CLOSED);
        // Event
        eventService.event(EventForm.requestMerged(
                request,
                branch,
                structureService.getProject(branch.getProjectId())
        ));
        // OK
        return version;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDashboard> dashboard() {
        // All projects
        List<ProjectSummary> projects = structureService.getProjects();
        // Transforms all projects into their dashboards
        return Lists.transform(
                projects,
                new Function<ProjectSummary, ProjectDashboard>() {
                    @Override
                    public ProjectDashboard apply(ProjectSummary p) {
                        return new ProjectDashboard(
                                p,
                                Lists.transform(
                                        structureService.getProjectBranches(p.getId()),
                                        new Function<BranchSummary, BranchDashboard>() {
                                            @Override
                                            public BranchDashboard apply(BranchSummary b) {
                                                return new BranchDashboard(
                                                        b,
                                                        getLastOpenRequestForBranch(b.getId())
                                                );
                                            }
                                        }
                                )
                        );
                    }
                }
        );
    }

    private RequestSummary getLastOpenRequestForBranch(int branchId) {
        TRequest lastForBranch = requestDao.findLastForBranch(branchId);
        return lastForBranch != null ? requestSummaryFn.apply(lastForBranch) : null;
    }

    protected TranslationMap readResponse(Configured<Object, TxFileExchange<Object>> configuredTxFileExchange, Locale defaultLocale, Set<Locale> supportedLocales, MultipartFile response) {
        // Content
        NamedContent content;
        try {
            content = new NamedContent(
                    response.getOriginalFilename(),
                    response.getContentType(),
                    response.getBytes()
            );
        } catch (IOException e) {
            throw new RequestUploadIOException(e, response.getName());
        }
        // Reads the content
        return configuredTxFileExchange.getConfigurable().read(configuredTxFileExchange.getConfiguration(), defaultLocale, supportedLocales, content);
    }

    protected List<TranslationDiffControl> controlEntry(Locale outputLocale, TranslationDiffEntry entry, Set<Locale> supportedLocales) {
        // Result
        Map<Integer, TranslationDiffControl> controls = new TreeMap<>();
        // According to the type
        switch (entry.getType()) {
            case ADDED:
                // Checks that all values are filled in
                for (Locale supportedLocale : supportedLocales) {
                    String suppliedValue = entry.getNewValue(supportedLocale);
                    if (StringUtils.isBlank(suppliedValue)) {
                        addControl(controls, entry, outputLocale, RequestService.MISSING_LOCALE_IN_KEY, supportedLocale);
                    }
                }
                break;
            case UPDATED:
                // Checks that all values are filled in and different from the old value
                for (Locale supportedLocale : supportedLocales) {
                    String oldValue = entry.getOldValue(supportedLocale);
                    String newValue = entry.getNewValue(supportedLocale);
                    if (StringUtils.isBlank(newValue)) {
                        addControl(controls, entry, outputLocale, RequestService.MISSING_LOCALE_IN_KEY, supportedLocale);
                    } else if (StringUtils.equals(oldValue, newValue)) {
                        addControl(controls, entry, outputLocale, RequestService.UNCHANGED_LOCALE_IN_KEY, supportedLocale);
                    }
                }
                break;
            case DELETED:
                // No control is needed
                break;
        }
        // OK
        return new ArrayList<>(controls.values());
    }

    protected void addControl(Map<Integer, TranslationDiffControl> controls, TranslationDiffEntry entry, Locale outputLocale, String code, Locale locale) {
        TranslationDiffControl control = controls.get(entry.getEntryId());
        if (control == null) {
            control = new TranslationDiffControl(entry.getEntryId(), entry.getBundle(), entry.getSection(), entry.getKey());
            controls.put(entry.getEntryId(), control);
        }
        control.add(locale, strings.get(outputLocale, code, locale));
    }
}
