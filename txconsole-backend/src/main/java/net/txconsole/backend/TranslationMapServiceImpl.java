package net.txconsole.backend;

import com.google.common.base.Function;
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
    public TranslationDiff diff(Locale referenceLocale, TranslationMap oldMap, TranslationMap newMap) {
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
            diffBundle(referenceLocale, diffBuilder, bundleName, oldBundle, newBundle);
        }
        // OK
        return diffBuilder.build();
    }

    protected void diffBundle(Locale referenceLocale, TranslationDiffBuilder diffBuilder, String bundleName, Bundle oldBundle, Bundle newBundle) {
        // Collects the index of values per pair of (section x key)
        Map<Pair<String, String>, Map<Locale, BundleValue>> oldValues = getValueIndex(oldBundle);
        Map<Pair<String, String>, Map<Locale, BundleValue>> newValues = getValueIndex(newBundle);

        // Keys only in old ==> deleted keys
        Set<Pair<String, String>> deletedKeys = new HashSet<>(oldValues.keySet());
        deletedKeys.removeAll(newValues.keySet());
        for (Pair<String, String> deletedKey : deletedKeys) {
            diffBuilder.deleted(
                    bundleName,
                    deletedKey.getLeft(),
                    deletedKey.getRight(),
                    Maps.transformValues(
                            oldValues.get(deletedKey),
                            TranslationMap.bundleValueFn
                    )
            );
        }

        // Keys only in new ==> added keys
        Set<Pair<String, String>> addedKeys = new HashSet<>(newValues.keySet());
        addedKeys.removeAll(oldValues.keySet());
        for (Pair<String, String> addedKey : addedKeys) {
            diffBuilder.added(
                    bundleName,
                    addedKey.getLeft(),
                    addedKey.getRight(),
                    Maps.transformValues(
                            newValues.get(addedKey),
                            TranslationMap.bundleValueFn
                    )
            );
        }

        // Keys in both ==> potentially updated
        Set<Pair<String, String>> commonKeys = new HashSet<>(newValues.keySet());
        commonKeys.retainAll(oldValues.keySet());
        for (Pair<String, String> commonKey : commonKeys) {
            Map<Locale, BundleValue> oldValue = oldValues.get(commonKey);
            Map<Locale, BundleValue> newValue = newValues.get(commonKey);
            // Gets the reference value
            String oldLabel = getValue(oldValue, referenceLocale);
            String newLabel = getValue(newValue, referenceLocale);
            // If different
            if (!StringUtils.equals(oldLabel, newLabel)) {
                // Old null ==> new key
                if (oldLabel == null) {
                    diffBuilder.added(
                            bundleName,
                            commonKey.getLeft(),
                            commonKey.getRight(),
                            Maps.transformValues(
                                    newValues.get(commonKey),
                                    TranslationMap.bundleValueFn
                            )
                    );
                }
                // New null ==> deleted key
                else if (newLabel == null) {
                    diffBuilder.deleted(
                            bundleName,
                            commonKey.getLeft(),
                            commonKey.getRight(),
                            Maps.transformValues(
                                    oldValues.get(commonKey),
                                    TranslationMap.bundleValueFn
                            )
                    );
                }
                // Just different ==> update
                else {
                    diffBuilder.updated(
                            bundleName,
                            commonKey.getLeft(),
                            commonKey.getRight(),
                            Maps.transformValues(
                                    oldValues.get(commonKey),
                                    TranslationMap.bundleValueFn
                            ),
                            Maps.transformValues(
                                    newValues.get(commonKey),
                                    TranslationMap.bundleValueFn
                            )
                    );
                }
            }
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
