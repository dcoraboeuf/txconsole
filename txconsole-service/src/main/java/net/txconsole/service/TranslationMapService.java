package net.txconsole.service;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.model.TranslationMapRequest;

public interface TranslationMapService {

    TranslationMap request(int branchId, TranslationMapRequest request);

}
