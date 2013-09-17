package net.txconsole.core.model;

import lombok.Data;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

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
}
