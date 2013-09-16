package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import net.sf.jstring.model.Bundle;
import net.sf.jstring.model.BundleKey;
import net.sf.jstring.model.BundleSection;
import net.sf.jstring.model.BundleValue;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffBuilder;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TranslationMapServiceImpl implements TranslationMapService {

    private final StructureService structureService;
    /**
     * Bundle -&gt; name
     */
    private final Function<Bundle, String> bundleNameFn = new Function<Bundle, String>() {
        @Override
        public String apply(Bundle bundle) {
            return bundle.getName();
        }
    };

    @Autowired
    public TranslationMapServiceImpl(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationMap map(int branchId, String version) {
        // Gets the branch configuration
        Configured<Object, TranslationSource<Object>> txConfigured = structureService.getConfiguredTranslationSource(branchId);
        // Reads the map
        return txConfigured.getConfigurable().read(txConfigured.getConfiguration(), version);
    }

    @Override
    public TranslationDiff diff(TranslationMap oldMap, TranslationMap newMap) {
        TranslationDiffBuilder diffBuilder = TranslationDiffBuilder.create();
        // Bundle names
        Map<String, Bundle> oldBundles = Maps.uniqueIndex(oldMap.getBundleCollection().getBundles(), bundleNameFn);
        Map<String, Bundle> newBundles = Maps.uniqueIndex(newMap.getBundleCollection().getBundles(), bundleNameFn);
        // Gets only the entries in common - any added or removed bundle is simply ignored
        Set<String> bundleNames = new HashSet<>(oldBundles.keySet());
        bundleNames.retainAll(newBundles.keySet());
        // Loops through the bundle names and compare bundles between each other
        for (String bundleName : bundleNames) {
            Bundle oldBundle = oldBundles.get(bundleName);
            Bundle newBundle = newBundles.get(bundleName);
            diffBundle(diffBuilder, bundleName, oldBundle, newBundle);
        }
        // OK
        return diffBuilder.build();
    }

    protected void diffBundle(TranslationDiffBuilder diffBuilder, String bundleName, Bundle oldBundle, Bundle newBundle) {
        // Collects the index of values per pair of (section x key)
        Map<Pair<String, String>, Map<Locale, BundleValue>> oldValues = getValueIndex(oldBundle);
        Map<Pair<String, String>, Map<Locale, BundleValue>> newValues = getValueIndex(newBundle);

        // Gets the differences
        MapDifference<Pair<String, String>, Map<Locale, BundleValue>> difference = Maps.difference(oldValues, newValues);

        // ADDED keys
        for (Map.Entry<Pair<String, String>, Map<Locale, BundleValue>> entry : difference.entriesOnlyOnRight().entrySet()) {
            diffBuilder.added(bundleName, entry.getKey().getLeft(), entry.getKey().getRight(),
                    Maps.transformValues(
                            entry.getValue(),
                            TranslationMap.bundleValueFn
                    )
            );
        }

        // DELETED keys
        for (Map.Entry<Pair<String, String>, Map<Locale, BundleValue>> entry : difference.entriesOnlyOnLeft().entrySet()) {
            diffBuilder.deleted(bundleName, entry.getKey().getLeft(), entry.getKey().getRight(),
                    Maps.transformValues(
                            entry.getValue(),
                            TranslationMap.bundleValueFn
                    )
            );
        }

        // UPDATED keys
        for (Map.Entry<Pair<String, String>, Map<Locale, BundleValue>> entry : difference.entriesInCommon().entrySet()) {
            diffValues(diffBuilder, bundleName, entry.getKey().getLeft(), entry.getKey().getRight(),
                    oldValues.get(entry.getKey()),
                    newValues.get(entry.getKey()));
        }

        //To change body of created methods use File | Settings | File Templates.
    }

    protected void diffValues(TranslationDiffBuilder diffBuilder, String bundleName, String section, String key, Map<Locale, BundleValue> oldValues, Map<Locale, BundleValue> newValues) {
        // TODO Default locale
        Locale defaultLocale = Locale.ENGLISH;
        // Gets the default value
        String oldValue = getValue(oldValues, defaultLocale);
        String newValue = getValue(newValues, defaultLocale);
        // Different?
        if (!StringUtils.equals(oldValue, newValue)) {
            diffBuilder.updated(bundleName, section, key,
                    Maps.transformValues(
                            oldValues,
                            TranslationMap.bundleValueFn
                    ),
                    Maps.transformValues(
                            newValues,
                            TranslationMap.bundleValueFn
                    )
            );
        }
    }

    protected String getValue(Map<Locale, BundleValue> values, Locale locale) {
        BundleValue bundleValue = values.get(locale);
        return bundleValue != null ? bundleValue.getValue() : null;
    }

    protected Map<Pair<String, String>, Map<Locale, BundleValue>> getValueIndex(Bundle bundle) {
        Map<Pair<String, String>, Map<Locale, BundleValue>> values = new HashMap<>();
        for (BundleSection section : bundle.getSections()) {
            for (BundleKey key : section.getKeys()) {
                values.put(
                        Pair.of(section.getName(), key.getName()),
                        key.getValues()
                );
            }
        }
        return values;
    }

}
