package net.txconsole.core.model;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffEntry {

    private final String bundle;
    private final String section;
    private final String key;
    private final TranslationDiffType type;
    private final Map<Locale, Pair<String, String>> values;

}
