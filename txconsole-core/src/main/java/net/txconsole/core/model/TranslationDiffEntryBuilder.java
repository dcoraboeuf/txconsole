package net.txconsole.core.model;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffEntryBuilder {

    private final int entryId;
    private final String bundle;
    private final String section;
    private final String key;
    private final TranslationDiffType type;
    private final Map<Locale, TranslationDiffEntryValue> values = new HashMap<>();

    public TranslationDiffEntryBuilder withDiff(Locale locale, String oldValue, String newValue) {
        return withDiff(0, locale, oldValue, newValue);
    }

    public TranslationDiffEntry build() {
        return new TranslationDiffEntry(
                entryId,
                bundle,
                section,
                key,
                type,
                ImmutableMap.copyOf(values)
        );
    }

    public TranslationDiffEntryBuilder withDiff(int entryValueId, Locale locale, String oldValue, String newValue) {
        values.put(locale, new TranslationDiffEntryValue(entryValueId, locale, oldValue, newValue));
        return this;
    }
}
