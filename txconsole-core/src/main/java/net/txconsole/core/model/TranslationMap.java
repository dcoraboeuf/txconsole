package net.txconsole.core.model;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import java.util.Map;
import java.util.TreeMap;

/**
 * Defines an access to an annotated map of
 * translations, where keys are associated with
 * categories, groups and descriptions, and labels
 * are associated with different languages.
 */
public class TranslationMap {

    private final Table<TranslationKey, String, String> table;

    public TranslationMap() {
        table = Tables.newCustomTable(
                new TreeMap<TranslationKey, Map<String, String>>(),
                new Supplier<Map<String, String>>() {
                    @Override
                    public Map<String, String> get() {
                        return new TreeMap<>();
                    }
                }
        );
    }
}
