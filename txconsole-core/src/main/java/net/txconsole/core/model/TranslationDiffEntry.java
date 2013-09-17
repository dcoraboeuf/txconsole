package net.txconsole.core.model;

import com.google.common.collect.Iterables;
import lombok.Data;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

@Data
public class TranslationDiffEntry implements Comparable<TranslationDiffEntry> {

    private final int entryId;
    private final String bundle;
    private final String section;
    private final String key;
    private final TranslationDiffType type;
    private final Map<Locale, TranslationDiffEntryValue> values;

    @JsonIgnore
    public Map<Locale, TranslationDiffEntryValue> getValues() {
        return values;
    }

    public Collection<TranslationDiffEntryValue> getEntries() {
        return values.values();
    }

    @Override
    public int compareTo(TranslationDiffEntry o) {
        return new CompareToBuilder()
                .append(this.bundle, o.bundle)
                .append(this.section, o.section)
                .append(this.key, o.key)
                .toComparison();
    }

    public TranslationDiffEntry forEdition(Collection<Locale> locales) {
        if (type == TranslationDiffType.ADDED) {
            // Added ==> editable if some locales are missing
            Set<Locale> missingLocales = new HashSet<>(locales);
            missingLocales.removeAll(values.keySet());
            if (missingLocales.isEmpty()) {
                // Nothing to edit
                return null;
            } else {
                return withMissingLocales(missingLocales);
            }
        } else if (type == TranslationDiffType.UPDATED) {
            // Updated ==> editable is some locales are missing OR if one value is editable
            boolean editable = isEditable();
            Set<Locale> missingLocales = new HashSet<>(locales);
            missingLocales.removeAll(values.keySet());
            if (editable || !missingLocales.isEmpty()) {
                return withMissingLocales(missingLocales);
            } else {
                return null;
            }

        } else {
            // Deleted ==> not editable
            return null;
        }
    }

    private boolean isEditable() {
        return Iterables.any(
                values.values(),
                TranslationDiffEntryValue.entryValueEditableFn
        );
    }

    private TranslationDiffEntry withMissingLocales(Set<Locale> missingLocales) {
        Map<Locale, TranslationDiffEntryValue> newValues = new HashMap<>(values);
        // Provides the missing locales
        for (Locale missingLocale : missingLocales) {
            newValues.put(
                    missingLocale,
                    new TranslationDiffEntryValue(
                            missingLocale,
                            true,
                            null,
                            null
                    )
            );
        }
        // OK
        return withNewValues(newValues);
    }

    private TranslationDiffEntry withNewValues(Map<Locale, TranslationDiffEntryValue> newValues) {
        return new TranslationDiffEntry(
                entryId,
                bundle,
                section,
                key,
                type,
                newValues
        );
    }
}
