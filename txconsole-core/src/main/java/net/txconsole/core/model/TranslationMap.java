package net.txconsole.core.model;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import lombok.Data;

import java.util.*;

/**
 * Defines an access to an annotated map of
 * translations, where keys are associated with
 * categories, groups and descriptions, and labels
 * are associated with different languages.
 */
public class TranslationMap {

    private final Table<TranslationKey, Locale, String> table;

    public TranslationMap() {
        table = Tables.newCustomTable(
                new TreeMap<TranslationKey, Map<Locale, String>>(),
                new Supplier<Map<Locale, String>>() {
                    @Override
                    public Map<Locale, String> get() {
                        return new TreeMap<>(new Comparator<Locale>() {
                            @Override
                            public int compare(Locale o1, Locale o2) {
                                return o1.toString().compareTo(o2.toString());
                            }
                        });
                    }
                }
        );
    }

    public void insert(TranslationKey key, Locale locale, String label) {
        table.put(key, locale, label);
    }

    public TranslationFlatMap toFlatMap() {
        return new TranslationFlatMap(
                new HashSet<>(table.columnKeySet()),
                new TreeMap<>(table.rowMap())
        );
    }

    @Data
    public static class TranslationFlatMap {
        private final Set<Locale> locales;
        private final Map<TranslationKey, Map<Locale, String>> table;
    }
}
