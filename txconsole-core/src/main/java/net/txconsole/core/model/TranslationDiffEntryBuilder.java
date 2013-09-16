package net.txconsole.core.model;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffEntryBuilder {

    private final String bundle;
    private final String section;
    private final String key;
    private final TranslationDiffType type;
    private final Map<Locale, Pair<String, String>> values = new HashMap<>();

    public TranslationDiffEntryBuilder withDiff(Locale locale, String oldValue, String newValue) {
        values.put(locale, Pair.of(oldValue, newValue));
        return this;
    }

    public TranslationDiffEntry build() {
        return new TranslationDiffEntry(
                bundle,
                section,
                key,
                type,
                ImmutableMap.copyOf(values)
        );
    }
}
