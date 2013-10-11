package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.sf.jstring.builder.*;
import net.sf.jstring.model.*;
import net.sf.jstring.support.KeyIdentifier;
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
@ToString
@EqualsAndHashCode
public class TranslationMap {

    public static final Function<BundleValue, String> bundleValueFn = new Function<BundleValue, String>() {
        @Override
        public String apply(BundleValue bundleValue) {
            return bundleValue.getValue();
        }
    };
    private final String version;
    private final BundleCollection bundleCollection;
    private final Set<Locale> supportedLocales;

    public TranslationMap(String version, BundleCollection bundleCollection) {
        this.version = version;
        this.bundleCollection = bundleCollection;
        // Gets the list of supported locales
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
        supportedLocales = ImmutableSet.copyOf(locales);
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
        return supportedLocales;
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
                                new KeyIdentifier(bundle.getName(), bundleSection.getName(), bundleKey.getName()),
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

    public TranslationMap applyDiff(TranslationDiff diff) {

        // Deletion of keys
        final Set<KeyIdentifier> keysToDelete = new HashSet<>();
        for (TranslationDiffEntry entry : diff.getEntries()) {
            if (entry.getType() == TranslationDiffType.DELETED) {
                KeyIdentifier tkey = new KeyIdentifier(
                        entry.getBundle(),
                        entry.getSection(),
                        entry.getKey()
                );
                keysToDelete.add(tkey);
            }
        }

        // Removes all deleted keys from the bundle collection
        BundleCollection collection = bundleCollection.filter(new Predicate<KeyIdentifier>() {
            @Override
            public boolean apply(KeyIdentifier i) {
                return !keysToDelete.contains(i);
            }
        });
        TranslationMap map = new TranslationMap(version, collection);

        // For the added & updated keys, we can just create a map and merge it
        TranslationMap diffMap = asMap(diff);

        // Returns the merged map
        return map.merge(diffMap);
    }

    public TranslationMap merge(TranslationMap map) {
        // Indexes the source bundles
        ImmutableMap<String, Bundle> sourceBundleIndex = Maps.uniqueIndex(map.getBundleCollection().getBundles(), Bundle.bundleNameFn);
        // Indexes the target bundles
        ImmutableMap<String, Bundle> targetBundleIndex = Maps.uniqueIndex(this.getBundleCollection().getBundles(), Bundle.bundleNameFn);
        // Builder for the result
        BundleCollectionBuilder resultBuilder = BundleCollectionBuilder.create();
        // Set of all bundle keys
        Set<String> bundleNames = new HashSet<>(sourceBundleIndex.keySet());
        bundleNames.addAll(targetBundleIndex.keySet());
        // For each bundle name
        for (String bundleName : bundleNames) {
            // Source & target bundles
            Bundle sourceBundle = sourceBundleIndex.get(bundleName);
            Bundle targetBundle = targetBundleIndex.get(bundleName);
            // Only source bundle
            if (targetBundle == null) {
                // Adds to the result
                resultBuilder.bundle(sourceBundle);
            }
            // Only target bundle
            else if (sourceBundle == null) {
                // Adds to the result
                resultBuilder.bundle(targetBundle);
            }
            // Both exists
            else {
                // Bundles need to be merged
                BundleBuilder bundleBuilder = BundleBuilder.create(bundleName);
                bundleBuilder.merge(targetBundle, BundleValueMergeMode.REPLACE);
                bundleBuilder.merge(sourceBundle, BundleValueMergeMode.REPLACE);
                resultBuilder.bundle(bundleBuilder.build());
            }
        }
        // OK
        return new TranslationMap(
                version,
                resultBuilder.build()
        );
    }

    public static class FilterPredicate implements Predicate<TranslationEntry> {

        private final Pattern filter;

        public FilterPredicate(String filter) {
            this.filter = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        }

        @Override
        public boolean apply(TranslationEntry entry) {
            String key = entry.getKey().getKey();
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
