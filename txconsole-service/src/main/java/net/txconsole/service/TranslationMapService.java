package net.txconsole.service;

import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationMap;

import java.util.Locale;

public interface TranslationMapService {

    TranslationMap map(int branchId, String version);

    TranslationDiff diff(Locale referenceLocale, TranslationMap oldMap, TranslationMap newMap);
}
