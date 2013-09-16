package net.txconsole.service;

import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationMap;

public interface TranslationMapService {

    TranslationMap map(int branchId, String version);

    TranslationDiff diff(TranslationMap oldMap, TranslationMap newMap);
}
