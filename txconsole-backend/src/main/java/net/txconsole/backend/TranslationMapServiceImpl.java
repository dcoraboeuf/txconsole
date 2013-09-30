package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import net.sf.jstring.model.Bundle;
import net.sf.jstring.model.BundleKey;
import net.sf.jstring.model.BundleSection;
import net.sf.jstring.model.BundleValue;
import net.sf.jstring.support.KeyIdentifier;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffBuilder;
import net.txconsole.core.model.TranslationDiffEntryBuilder;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(readOnly = true, propagation = Propagation.NESTED)
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
            diffBundle(referenceLocale, diffBuilder, oldBundle, newBundle);
        }
        // OK
        return diffBuilder.build();
    }

    protected void diffBundle(Locale referenceLocale, TranslationDiffBuilder diffBuilder, Bundle oldBundle, Bundle newBundle) {

        // Collects the index of default values per (bundle x section x key)
        Map<KeyIdentifier, String> oldValues = getReferenceValueIndex(oldBundle, referenceLocale);
        Map<KeyIdentifier, String> newValues = getReferenceValueIndex(newBundle, referenceLocale);

        // Collects the index of all keys
        Map<KeyIdentifier, BundleKey> oldBundleIndex = getKeyIndex(oldBundle);
        Map<KeyIdentifier, BundleKey> newBundleIndex = getKeyIndex(newBundle);

        // Keys only in old ==> deleted keys
        Set<KeyIdentifier> deletedKeys = new HashSet<>(oldValues.keySet());
        deletedKeys.removeAll(newValues.keySet());
        for (KeyIdentifier deletedKey : deletedKeys) {
            TranslationDiffEntryBuilder entry = diffBuilder.deleted(
                    deletedKey.getBundle(),
                    deletedKey.getSection(),
                    deletedKey.getKey());
            collectDiffs(entry, deletedKey, oldBundleIndex, newBundleIndex);
        }

        // Keys only in new ==> added keys
        Set<KeyIdentifier> addedKeys = new HashSet<>(newValues.keySet());
        addedKeys.removeAll(oldValues.keySet());
        for (KeyIdentifier addedKey : addedKeys) {
            TranslationDiffEntryBuilder entry = diffBuilder.added(
                    addedKey.getBundle(),
                    addedKey.getSection(),
                    addedKey.getKey());
            collectDiffs(entry, addedKey, oldBundleIndex, newBundleIndex);
        }

        // Keys in both ==> potentially updated
        Set<KeyIdentifier> commonKeys = new HashSet<>(newValues.keySet());
        commonKeys.retainAll(oldValues.keySet());
        for (KeyIdentifier commonKey : commonKeys) {
            // Gets the reference values in both maps
            String oldDefaultValue = oldValues.get(commonKey);
            String newDefaultValue = newValues.get(commonKey);
            // If different
            if (!StringUtils.equals(oldDefaultValue, newDefaultValue)) {
                // Old null ==> new key
                if (oldDefaultValue == null) {
                    TranslationDiffEntryBuilder entry = diffBuilder.added(
                            commonKey.getBundle(),
                            commonKey.getSection(),
                            commonKey.getKey()
                    );
                    collectDiffs(entry, commonKey, oldBundleIndex, newBundleIndex);
                }
                // New null ==> deleted key
                else if (newDefaultValue == null) {
                    TranslationDiffEntryBuilder entry = diffBuilder.deleted(
                            commonKey.getBundle(),
                            commonKey.getSection(),
                            commonKey.getKey()
                    );
                    collectDiffs(entry, commonKey, oldBundleIndex, newBundleIndex);
                }
                // Just different ==> update
                else {
                    TranslationDiffEntryBuilder entry = diffBuilder.updated(
                            commonKey.getBundle(),
                            commonKey.getSection(),
                            commonKey.getKey()
                    );
                    collectDiffs(entry, commonKey, oldBundleIndex, newBundleIndex);
                }
            }
        }
    }

    protected void collectDiffs(TranslationDiffEntryBuilder entry, KeyIdentifier deletedKey, Map<KeyIdentifier, BundleKey> oldBundleIndex, Map<KeyIdentifier, BundleKey> newBundleIndex) {
        BundleKey oldKeyValues = oldBundleIndex.get(deletedKey);
        BundleKey newKeyValues = newBundleIndex.get(deletedKey);
        Set<Locale> keyLocales = getKeyLocales(oldKeyValues);
        keyLocales.addAll(getKeyLocales(newKeyValues));
        for (Locale keyLocale : keyLocales) {
            String oldValue = getKeyValue(oldKeyValues, keyLocale);
            String newValue = getKeyValue(newKeyValues, keyLocale);
            entry = entry.withDiff(keyLocale, oldValue, newValue);
        }
    }

    protected String getKeyValue(BundleKey keyValues, Locale locale) {
        if (keyValues != null) {
            BundleValue keyValue = keyValues.getValues().get(locale);
            return keyValue != null ? keyValue.getValue() : null;
        } else {
            return null;
        }
    }

    protected Set<Locale> getKeyLocales(BundleKey keyValues) {
        if (keyValues == null) {
            return new HashSet<>();
        } else {
            return new HashSet<>(keyValues.getValues().keySet());
        }
    }

    protected Map<KeyIdentifier, String> getReferenceValueIndex(Bundle bundle, Locale referenceLocale) {
        Map<KeyIdentifier, String> index = new HashMap<>();
        for (BundleSection section : bundle.getSections()) {
            for (BundleKey key : section.getKeys()) {
                BundleValue bundleValue = key.getValues().get(referenceLocale);
                if (bundleValue != null) {
                    index.put(new KeyIdentifier(bundle.getName(), section.getName(), key.getName()), bundleValue.getValue());
                }
            }
        }
        return index;
    }

    protected Map<KeyIdentifier, BundleKey> getKeyIndex(Bundle bundle) {
        Map<KeyIdentifier, BundleKey> values = new HashMap<>();
        for (BundleSection section : bundle.getSections()) {
            for (BundleKey key : section.getKeys()) {
                values.put(new KeyIdentifier(bundle.getName(), section.getName(), key.getName()), key);
            }
        }
        return values;
    }

}
