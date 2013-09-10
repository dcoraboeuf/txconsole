package net.txconsole.backend;

import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.core.model.RequestConfigurationData;
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

    @Autowired
    public RequestServiceImpl(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    @Transactional(readOnly = true)
    public RequestConfigurationData getRequestConfigurationData(int branchId) {
        // Loading the structure information
        BranchSummary branchSummary = structureService.getBranch(branchId);
        ProjectSummary projectSummary = structureService.getProject(branchSummary.getProjectId());
        // Gets the configuration information
        Configured<Object,TranslationSource<Object>> configuredTranslationSource = structureService.getConfiguredTranslationSource(branchId);
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
}
