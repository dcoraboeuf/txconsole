package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.sf.jstring.support.KeyIdentifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class TranslationDiffBuilder {

    private final Map<KeyIdentifier, TranslationDiffEntryBuilder> entries = new LinkedHashMap<>();

    public static TranslationDiffBuilder create() {
        return new TranslationDiffBuilder();
    }

    public TranslationDiff build() {
        return new TranslationDiff(
                Lists.newArrayList(
                        Collections2.transform(
                                entries.values(),
                                new Function<TranslationDiffEntryBuilder, TranslationDiffEntry>() {
                                    @Override
                                    public TranslationDiffEntry apply(TranslationDiffEntryBuilder builder) {
                                        return builder.build();
                                    }
                                }
                        )
                )
        );
    }

    public TranslationDiffEntryBuilder entry(int entryId, String bundle, String section, String key, TranslationDiffType type) {
        KeyIdentifier ki = new KeyIdentifier(bundle, section, key);
        TranslationDiffEntryBuilder entry = entries.get(ki);
        if (entry == null) {
            entry = new TranslationDiffEntryBuilder(
                    entryId,
                    bundle,
                    section,
                    key,
                    type
            );
            entries.put(ki, entry);
        }
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
