package net.txconsole.backend;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.model.TranslationMapRequest;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import org.apache.commons.lang3.StringUtils;
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
    public TranslationMap request(int branchId, TranslationMapRequest request) {
        // Gets the branch configuration
        Configured<Object, TranslationSource<Object>> txConfigured = structureService.getConfiguredTranslationSource(branchId);
        // Reads the map
        // TODO Cache: branch x version
        TranslationMap rawMap = txConfigured.getConfigurable().read(txConfigured.getConfiguration(), request.getVersion());
        // Applies the filter
        if (StringUtils.isNotBlank(request.getFilter())) {
            return filterMap(rawMap, request.getFilter());
        } else {
            // No filter
            // TODO Filters on maximum count
            return rawMap;
        }
    }

    protected TranslationMap filterMap(TranslationMap map, String filter) {
        // FIXME Applies the filter (think generic, by having an interface for the filtering on a 'translation row',
        // a key and its labels
        return map;
    }

}
