package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffEntry {

    private final String bundle;
    private final String section;
    private final String key;
    private final TranslationDiffType type;
    private final Map<Locale, String> oldValues;
    private final Map<Locale, String> newValues;

}
