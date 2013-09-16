package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TranslationDiffBuilder {

    private final List<TranslationDiffEntry> entries = new ArrayList<>();
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
        return new TranslationDiff(entries);
    }

    public void added(String bundle, String section, String key, Map<Locale, String> values) {
        entries.add(
                new TranslationDiffEntry(
                        bundle,
                        section,
                        key,
                        TranslationDiffType.ADDED,
                        Maps.transformValues(
                                values,
                                newValuePairFn
                        )
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
                        Maps.transformValues(
                                values,
                                oldValuePairFn
                        )
                )
        );
    }

    public void updated(String bundle, String section, String key, Map<Locale, String> oldValues, Map<Locale, String> newValues) {
        // Diff
        Map<Locale, Pair<String, String>> diff = Maps.transformValues(
                Maps.difference(oldValues, newValues).entriesDiffering(),
                new Function<MapDifference.ValueDifference<String>, Pair<String, String>>() {
                    @Override
                    public Pair<String, String> apply(MapDifference.ValueDifference<String> d) {
                        return Pair.of(d.leftValue(), d.rightValue());
                    }
                }
        );
        // OK
        entries.add(
                new TranslationDiffEntry(
                        bundle,
                        section,
                        key,
                        TranslationDiffType.UPDATED,
                        diff
                )
        );
    }
}
