package net.txconsole.core.model;

import java.util.*;

public class TranslationDiffBuilder {

    private final List<TranslationDiffEntry> entries = new ArrayList<>();

    public static TranslationDiffBuilder create() {
        return new TranslationDiffBuilder();
    }

    public TranslationDiff build() {
        return new TranslationDiff(entries);
    }

    public void added(String bundle, String section, String key, Map<Locale, String> values) {
        entries.add(
                new TranslationDiffEntry(
                        bundle,
                        section,
                        key,
                        TranslationDiffType.ADDED,
                        Collections.<Locale, String>emptyMap(),
                        values
                )
        );
    }

    public void deleted(String bundle, String section, String key, Map<Locale, String> values) {
        entries.add(
                new TranslationDiffEntry(
                        bundle,
                        section,
                        key,
                        TranslationDiffType.DELETED,
                        values,
                        Collections.<Locale, String>emptyMap()
                )
        );
    }

    public void updated(String bundle, String section, String key, Map<Locale, String> oldValues, Map<Locale, String> newValues) {
        entries.add(
                new TranslationDiffEntry(
                        bundle,
                        section,
                        key,
                        TranslationDiffType.UPDATED,
                        oldValues,
                        newValues
                )
        );
    }
}
