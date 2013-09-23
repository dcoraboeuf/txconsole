package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.sf.jstring.model.*;
import net.txconsole.core.support.LocaleComparator;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

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
    private final String version;
    private final BundleCollection bundleCollection;

    public TranslationMap(String version, BundleCollection bundleCollection) {
        this.version = version;
        this.bundleCollection = bundleCollection;
    }

    public static TranslationMap asMap(TranslationDiff diff) {
        // Map of bundles
        Map<String, BundleBuilder> bundleBuilderMap = new HashMap<>();
        // Map of sections per bundle x section
        Table<String, String, BundleSectionBuilder> bundleSectionBuilderTable = Tables.newCustomTable(
                new HashMap<String, Map<String, BundleSectionBuilder>>(),
                new Supplier<Map<String, BundleSectionBuilder>>() {
                    @Override
                    public Map<String, BundleSectionBuilder> get() {
                        return new HashMap<>();
                    }
                }
        );
        // For each entry
        for (TranslationDiffEntry entry : diff.getEntries()) {
            // Adds the diff new value if ADDED or UPDATED
            if (entry.getType() == TranslationDiffType.UPDATED || entry.getType() == TranslationDiffType.ADDED) {
                String bundle = entry.getBundle();
                String section = entry.getSection();
                String key = entry.getKey();
                // Gets the bundle builder
                BundleBuilder bundleBuilder = bundleBuilderMap.get(bundle);
                if (bundleBuilder == null) {
                    bundleBuilder = BundleBuilder.create(bundle);
                    bundleBuilderMap.put(bundle, bundleBuilder);
                }
                // Gets the section builder
                BundleSectionBuilder bundleSectionBuilder = bundleSectionBuilderTable.get(bundle, section);
                if (bundleSectionBuilder == null) {
                    bundleSectionBuilder = BundleSectionBuilder.create(section);
                    bundleBuilder.section(bundleSectionBuilder);
                    bundleSectionBuilderTable.put(bundle, section, bundleSectionBuilder);
                }
                // Key builder
                BundleKeyBuilder bundleKeyBuilder = bundleSectionBuilder.key(key);
                for (TranslationDiffEntryValue entryValue : entry.getEntries()) {
                    Locale locale = entryValue.getLocale();
                    String value = entryValue.getNewValue();
                    if (StringUtils.isNotBlank(value)) {
                        bundleKeyBuilder.addValue(locale, value);
                    }
                }
            }
        }
        // Collection builder
        BundleCollectionBuilder builder = BundleCollectionBuilder.create();
        // Adds all the bundles
        for (BundleBuilder bundleBuilder : bundleBuilderMap.values()) {
            builder.bundle(bundleBuilder.build());
        }
        // OK
        return new TranslationMap(
                null,
                builder.build()
        );
    }

    public String getVersion() {
        return version;
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

    public Set<Locale> getSupportedLocales() {
        Set<Locale> locales = new TreeSet<>(LocaleComparator.INSTANCE);
        // For all bundles
        for (Bundle bundle : bundleCollection.getBundles()) {
            // For all sections
            for (BundleSection bundleSection : bundle.getSections()) {
                // For all keys
                for (BundleKey bundleKey : bundleSection.getKeys()) {
                    // Locales
                    locales.addAll(bundleKey.getValues().keySet());
                }
            }
        }
        // OK
        return locales;
    }

    public TranslationMapResponse filter(int limit, Predicate<TranslationEntry> entryPredicate) {
        // Limit must be set
        if (limit <= 0) {
            throw new IllegalArgumentException("Filter limit must be set");
        }
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

    public TranslationMap merge(TranslationDiff diff) {
        // Diff as a map
        TranslationMap diffMap = asMap(diff);
        // Merge
        return merge(diffMap);
    }

    public TranslationMap merge(TranslationMap map) {
        // Builder
        BundleCollectionBuilder builder = BundleCollectionBuilder.create();
        // Adds the current bundles
        for (Bundle bundle : bundleCollection.getBundles()) {
            builder.bundle(bundle);
        }
        // Merges the other map
        builder.merge(map.getBundleCollection());
        // OK
        return new TranslationMap(
                version,
                builder.build()
        );
    }

    public static class FilterPredicate implements Predicate<TranslationEntry> {

        private final Pattern filter;

        public FilterPredicate(String filter) {
            this.filter = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        }

        @Override
        public boolean apply(TranslationEntry entry) {
            String key = entry.getKey().getName();
            if (StringUtils.isNotBlank(key) && filter.matcher(key).find()) {
                return true;
            } else {
                for (String label : entry.getLabels().values()) {
                    if (StringUtils.isNotBlank(label) && filter.matcher(label).find()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

}
