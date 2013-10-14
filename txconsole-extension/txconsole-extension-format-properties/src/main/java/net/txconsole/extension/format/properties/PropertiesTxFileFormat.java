package net.txconsole.extension.format.properties;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.sf.jstring.model.Bundle;
import net.sf.jstring.model.BundleKey;
import net.sf.jstring.model.BundleSection;
import net.sf.jstring.model.BundleValue;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.support.IOContext;
import net.txconsole.service.EscapingService;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.JDKEscapingService;
import net.txconsole.service.support.TxFileFormat;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static net.txconsole.extension.format.properties.PropertiesUtils.readProperties;

@Component
public class PropertiesTxFileFormat extends AbstractSimpleConfigurable<PropertiesTxFileFormatConfig> implements TxFileFormat<PropertiesTxFileFormatConfig> {

    private final EscapingService escapingService;

    protected PropertiesTxFileFormat(ObjectMapper objectMapper, EscapingService escapingService) {
        super(
                "extension-txfileformat-properties",
                "extension.format.properties",
                "extension.format.properties.description",
                PropertiesTxFileFormatConfig.class, objectMapper);
        this.escapingService = escapingService;
    }

    @Autowired
    public PropertiesTxFileFormat(ObjectMapper objectMapper) {
        this(objectMapper, new JDKEscapingService());
    }

    @Override
    public Locale getDefaultLocale(PropertiesTxFileFormatConfig config) {
        return config.getDefaultLocale();
    }

    @Override
    public Set<Locale> getSupportedLocales(PropertiesTxFileFormatConfig config) {
        Set<Locale> locales = new HashSet<>();
        for (PropertyGroup propertyGroup : config.getGroups()) {
            locales.addAll(propertyGroup.getLocales());
        }
        return locales;
    }

    @Override
    public TranslationMap readFrom(PropertiesTxFileFormatConfig config, IOContext context) {
        // Bundle collection builder
        BundleCollectionBuilder collectionBuilder = BundleCollectionBuilder.create();
        // For each group
        for (PropertyGroup propertyGroup : config.getGroups()) {
            String groupName = propertyGroup.getName();
            // Group ==> bundle
            BundleBuilder bundleBuilder = BundleBuilder.create(groupName);
            // Only one section for the properties
            BundleSectionBuilder sectionBuilder = bundleBuilder.getDefaultSectionBuilder();
            // For each supported locale
            for (Locale locale : propertyGroup.getLocales()) {
                // Gets the file name
                String name = String.format("%s_%s.properties", groupName, locale);
                // Loads the content of the property file
                Map<String, String> properties = loadProperties(context, name);
                // Loads into the map
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    String key = entry.getKey();
                    String label = entry.getValue();
                    // Gets the existing key builder, or create it
                    BundleKeyBuilder keyBuilder = sectionBuilder.key(key);
                    // Adds the key into the bundle
                    keyBuilder.addValue(locale, label);
                }
            }
            // Adds the bundle to the collection
            collectionBuilder.bundle(bundleBuilder.build());
        }
        // OK
        return new TranslationMap(context.getVersion(), collectionBuilder.build());
    }

    protected Map<String, String> loadProperties(IOContext context, String fileName) {
        // File
        File file = context.getFile(fileName);
        // If the file does not exist, that's an error
        if (!file.exists()) {
            throw new PropertyFileNotFoundException(fileName);
        } else {
            // Reads it
            Map<String, String> map = readProperties(file, "UTF-8", escapingService);
            // Ok
            return ImmutableMap.copyOf(map);
        }
    }

    @Override
    public void writeTo(PropertiesTxFileFormatConfig config, TranslationMap map, IOContext context) {
        // Index of values for bundle x locale
        Table<String, Locale, Map<String, String>> index = Tables.newCustomTable(
                new HashMap<String, Map<Locale, Map<String, String>>>(),
                new Supplier<Map<Locale, Map<String, String>>>() {
                    @Override
                    public Map<Locale, Map<String, String>> get() {
                        return new HashMap<>();
                    }
                }
        );
        for (Bundle bundle : map.getBundleCollection().getBundles()) {
            String bundleName = bundle.getName();
            for (BundleSection bundleSection : bundle.getSections()) {
                // Ignoring any section other than 'default'
                if (Bundle.DEFAULT_SECTION.equals(bundleSection.getName())) {
                    for (BundleKey bundleKey : bundleSection.getKeys()) {
                        for (Map.Entry<Locale, BundleValue> keyEntry : bundleKey.getValues().entrySet()) {
                            Locale locale = keyEntry.getKey();
                            String value = keyEntry.getValue().getValue();
                            Map<String, String> values = index.get(bundleName, locale);
                            if (values == null) {
                                values = new TreeMap<>(new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        if (StringUtils.equalsIgnoreCase(o1, o2)) {
                                            return o1.compareTo(o2);
                                        } else {
                                            return o1.compareToIgnoreCase(o2);
                                        }
                                    }
                                });
                                index.put(bundleName, locale, values);
                            }
                            values.put(bundleKey.getName(), value);
                        }
                    }
                }
            }
        }
        // Root dir
        File dir = context.getDir();
        // For all groups
        for (PropertyGroup propertyGroup : config.getGroups()) {
            String groupName = propertyGroup.getName();
            // For each supported locale
            for (Locale locale : propertyGroup.getLocales()) {
                // Gets the set of values for this group & locale
                Map<String, String> values = index.get(groupName, locale);
                if (values != null) {
                    // Target file
                    String fileName = String.format("%s_%s.properties", groupName, locale);
                    File file = new File(dir, fileName);
                    // Writes the properties into this file
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                        PropertiesUtils.writeProperties(out, values, escapingService);
                    } catch (IOException e) {
                        throw new PropertiesTxFileFormatIOException(fileName, e);
                    }
                }
            }
        }
    }
}
