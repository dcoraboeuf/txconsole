package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TranslationDiffBuilder {

    private final List<TranslationDiffEntryBuilder> entries = new ArrayList<>();
    private final Function<String, Pair<String, String>> newValuePairFn = new Function<String, Pair<String, String>>() {
        @Override
        public Pair<String, String> apply(String newValue) {
            return Pair.of(null, newValue);
        }
    };
    private final Function<String, Pair<String, String>> oldValuePairFn = new Function<String, Pair<String, String>>() {
        @Override
        public Pair<String, String> apply(String oldValue) {
            return Pair.of(oldValue, null);
        }
    };

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

    public TranslationDiffEntryBuilder added(String bundle, String section, String key) {
        TranslationDiffEntryBuilder entry = new TranslationDiffEntryBuilder(
                bundle,
                section,
                key,
                TranslationDiffType.ADDED
        );
        entries.add(entry);
        return entry;
    }

    public TranslationDiffEntryBuilder deleted(String bundle, String section, String key) {
        TranslationDiffEntryBuilder entry = new TranslationDiffEntryBuilder(
                bundle,
                section,
                key,
                TranslationDiffType.DELETED
        );
        entries.add(entry);
        return entry;
    }

    public TranslationDiffEntryBuilder updated(String bundle, String section, String key) {
        TranslationDiffEntryBuilder entry = new TranslationDiffEntryBuilder(
                bundle,
                section,
                key,
                TranslationDiffType.UPDATED
        );
        entries.add(entry);
        return entry;
    }
}
