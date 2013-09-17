package net.txconsole.core.model;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffEntryBuilder {

    private final String bundle;
    private final String section;
    private final String key;
    private final TranslationDiffType type;
    private final Map<Locale, TranslationDiffEntryValue> values = new HashMap<>();

    public TranslationDiffEntryBuilder withDiff(Locale locale, String oldValue, String newValue) {
        boolean toUpdate;
        if (type == TranslationDiffType.ADDED) {
            // If a new value has been provided, edition has already been done, no need to update
            toUpdate = StringUtils.isBlank(newValue);
        } else if (type == TranslationDiffType.UPDATED) {
            // If values are different, edition has already been done, no need to update
            toUpdate = StringUtils.equals(oldValue, newValue);
        } else {
            // Deleted key, no update needed
            toUpdate = false;
        }
        values.put(locale, new TranslationDiffEntryValue(locale, toUpdate, oldValue, newValue));
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
