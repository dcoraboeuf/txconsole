package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import net.sf.jstring.model.*;
import net.txconsole.core.support.LocaleComparator;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Defines an access to an annotated map of
 * translations, where keys are associated with
 * categories, groups and descriptions, and labels
 * are associated with different languages.
 */
public class TranslationMap {

    public static final Function<BundleValue, String> bundleValueFn = new Function<BundleValue, String>() {
        @Override
        public String apply(BundleValue bundleValue) {
            return bundleValue.getValue();
        }
    };

    public static class FilterPredicate implements Predicate<TranslationEntry> {

        private final String filter;

        public FilterPredicate(String filter) {
            this.filter = filter;
        }

        @Override
        public boolean apply(TranslationEntry entry) {
            if (StringUtils.contains(entry.getKey().getName(), filter)) {
                return true;
            } else {
                for (String label : entry.getLabels().values()) {
                    if (StringUtils.contains(label, filter)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private final BundleCollection bundleCollection;

    public TranslationMap(BundleCollection bundleCollection) {
        this.bundleCollection = bundleCollection;
    }

    public BundleCollection getBundleCollection() {
        return bundleCollection;
    }

    public TranslationMapResponse filter(int limit, String filter) {
        return filter(
                limit,
                StringUtils.isBlank(filter)
                        ? Predicates.<TranslationEntry>alwaysTrue()
                        : new FilterPredicate(filter)
        );
    }

    public TranslationMapResponse filter(int limit, Predicate<TranslationEntry> entryPredicate) {
        // Result
        int total = 0;
        int count = 0;
        Set<Locale> locales = new TreeSet<>(LocaleComparator.INSTANCE);
        List<TranslationEntry> entries = new ArrayList<>();
        // For all bundles
        for (Bundle bundle : bundleCollection.getBundles()) {
            // For all sections
            for (BundleSection bundleSection : bundle.getSections()) {
                // For all keys
                for (BundleKey bundleKey : bundleSection.getKeys()) {
                    // Total number of keys
                    total++;
                    // Locales
                    locales.addAll(bundleKey.getValues().keySet());
                    // Worth checking?
                    if (count < limit) {
                        // Creates the entry
                        TranslationEntry entry = new TranslationEntry(
                                new TranslationKey(bundle.getName(), bundleSection.getName(), bundleKey.getName()),
                                Maps.transformValues(
                                        bundleKey.getValues(),
                                        bundleValueFn
                                )
                        );
                        // Checks the entry
                        if (entryPredicate.apply(entry)) {
                            entries.add(entry);
                            count++;
                        }
                    }
                }
            }
        }
        // OK
        return new TranslationMapResponse(
                total,
                new ArrayList<>(locales),
                entries
        );
    }

}
