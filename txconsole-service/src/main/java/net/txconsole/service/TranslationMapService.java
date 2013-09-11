package net.txconsole.service;

import net.txconsole.core.model.TranslationMap;

public interface TranslationMapService {

    TranslationMap map(int branchId, String version);

}
