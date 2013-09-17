package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class TranslationDiffBuilder {

    private final List<TranslationDiffEntryBuilder> entries = new ArrayList<>();

    public static TranslationDiffBuilder create() {
        return new TranslationDiffBuilder();
    }

    public TranslationDiff build() {
        return new TranslationDiff(
                Lists.transform(
                        entries,
                        new Function<TranslationDiffEntryBuilder, TranslationDiffEntry>() {
                            @Override
                            public TranslationDiffEntry apply(TranslationDiffEntryBuilder builder) {
                                return builder.build();
                            }
                        }
                )
        );
    }

    public TranslationDiffEntryBuilder entry(int entryId, String bundle, String section, String key, TranslationDiffType type) {
        TranslationDiffEntryBuilder entry = new TranslationDiffEntryBuilder(
                entryId,
                bundle,
                section,
                key,
                type
        );
        entries.add(entry);
        return entry;
    }

    public TranslationDiffEntryBuilder entry(String bundle, String section, String key, TranslationDiffType type) {
        return entry(0, bundle, section, key, type);
    }

    public TranslationDiffEntryBuilder added(String bundle, String section, String key) {
        return entry(bundle, section, key, TranslationDiffType.ADDED);
    }

    public TranslationDiffEntryBuilder deleted(String bundle, String section, String key) {
        return entry(bundle, section, key, TranslationDiffType.DELETED);
    }

    public TranslationDiffEntryBuilder updated(String bundle, String section, String key) {
        return entry(bundle, section, key, TranslationDiffType.UPDATED);
    }
}
