package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

@Data
public class TranslationDiffEntry implements Comparable<TranslationDiffEntry> {

    public static final Function<TranslationDiffEntry, TranslationDiffEntry> entryTrimFn = new Function<TranslationDiffEntry, TranslationDiffEntry>() {
        @Override
        public TranslationDiffEntry apply(TranslationDiffEntry entry) {
            return entry.trimValues();
        }
    };
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

    public TranslationDiffEntry escape(final Function<String, String> escapeFn) {
        return withNewValues(
                Maps.transformValues(
                        values,
                        new Function<TranslationDiffEntryValue, TranslationDiffEntryValue>() {
                            @Override
                            public TranslationDiffEntryValue apply(TranslationDiffEntryValue entryValue) {
                                return entryValue.espace(escapeFn);
                            }
                        }
                )
        );
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

    @JsonIgnore
    public boolean isEditable() {
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
                            0,
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

    public TranslationDiffEntry trimValues() {
        return new TranslationDiffEntry(
                entryId,
                bundle,
                section,
                key,
                type,
                Collections.<Locale, TranslationDiffEntryValue>emptyMap()
        );
    }

    public TranslationDiffEntryValue getEntryValue(Locale locale) {
        return values.get(locale);
    }

    public String getNewValue(Locale locale) {
        TranslationDiffEntryValue entryValue = getEntryValue(locale);
        return entryValue != null ? entryValue.getNewValue() : null;
    }

    public String getOldValue(Locale locale) {
        TranslationDiffEntryValue entryValue = getEntryValue(locale);
        return entryValue != null ? entryValue.getOldValue() : null;
    }
}
