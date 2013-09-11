package net.txconsole.backend;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslationMapServiceImpl implements TranslationMapService {

    private final StructureService structureService;

    @Autowired
    public TranslationMapServiceImpl(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public TranslationMap map(int branchId, String version) {
        // Gets the branch configuration
        Configured<Object, TranslationSource<Object>> txConfigured = structureService.getConfiguredTranslationSource(branchId);
        // Reads the map
        // TODO Cache: branch x version
        // Note: if the version is blank, the actual version for the caching must be computed from the file source
        TranslationMap rawMap = txConfigured.getConfigurable().read(txConfigured.getConfiguration(), version);
        // OK
        return rawMap;
    }

    protected TranslationMap filterMap(TranslationMap map, String filter) {
        // FIXME Applies the filter (think generic, by having an interface for the filtering on a 'translation row',
        // a key and its labels
        return map;
    }

}
