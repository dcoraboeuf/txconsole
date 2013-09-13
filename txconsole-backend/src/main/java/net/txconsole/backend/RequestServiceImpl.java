package net.txconsole.backend;

import com.google.common.base.Function;
import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.EventService;
import net.txconsole.service.RequestService;
import net.txconsole.service.StructureService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestServiceImpl implements RequestService {

    private final StructureService structureService;
    private final EventService eventService;
    private final RequestDao requestDao;
    private final SecurityUtils securityUtils;
    /**
     * Request summary
     */
    private final Function<TRequest, RequestSummary> requestSummaryFn = new Function<TRequest, RequestSummary>() {
        @Override
        public RequestSummary apply(TRequest t) {
            return new RequestSummary(
                    t.getId(),
                    t.getBranchId(),
                    t.getVersion(),
                    t.getStatus()
            );
        }
    };

    @Autowired
    public RequestServiceImpl(StructureService structureService, EventService eventService, RequestDao requestDao, SecurityUtils securityUtils) {
        this.structureService = structureService;
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
        int requestId = requestDao.createRequest(branchId, form.getTxFileExchangeConfig());
        // Gets the request summary
        RequestSummary requestSummary = getRequest(requestId);
        // Event creation
        eventService.event(EventForm.requestCreated(
                requestSummary,
                branch,
                structureService.getProject(branch.getProjectId())
        ));
        // FIXME Launches the request creation job asynchronously
        // Returns the request summary
        return requestSummary;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestSummary getRequest(int requestId) {
        return requestSummaryFn.apply(requestDao.getById(requestId));
    }
}
