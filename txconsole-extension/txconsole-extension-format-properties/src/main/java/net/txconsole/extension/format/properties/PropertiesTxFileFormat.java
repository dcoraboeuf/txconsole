package net.txconsole.extension.format.properties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.support.UnicodeReader;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.TxFileFormat;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class PropertiesTxFileFormat extends AbstractSimpleConfigurable<PropertiesTxFileFormatConfig> implements TxFileFormat<PropertiesTxFileFormatConfig> {

    @Autowired
    public PropertiesTxFileFormat(ObjectMapper objectMapper) {
        super(
                "extension-txfileformat-properties",
                "extension.format.properties",
                "extension.format.properties.description",
                PropertiesTxFileFormatConfig.class, objectMapper);
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
        return new TranslationMap(collectionBuilder.build());
    }

    protected Map<String, String> loadProperties(IOContext context, String fileName) {
        // File
        File file = context.getFile(fileName);
        // If the file does not exist, that's an error
        if (!file.exists()) {
            throw new PropertyFileNotFoundException(fileName);
        } else {
            // Reads it
            Map<String, String> map = readProperties(file, "UTF-8");
            // Ok
            return ImmutableMap.copyOf(map);
        }
    }

    protected Map<String, String> readProperties(File file, String encoding) {
        try {
            try (FileInputStream in = new FileInputStream(file)) {
                return readProperties(in, encoding);
            }
        } catch (IOException ex) {
            throw new PropertyFileCannotReadException(file.getName(), ex);
        }
    }

    protected Map<String, String> readProperties(InputStream input, String encoding) throws IOException {
        BufferedReader in = new BufferedReader(new UnicodeReader(input, encoding));
        Properties properties = new Properties();
        properties.load(in);
        Map<String, String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (StringUtils.equalsIgnoreCase(o1, o2)) {
                    return o1.compareTo(o2);
                } else {
                    return o1.compareToIgnoreCase(o2);
                }
            }
        });
        map.putAll(Maps.fromProperties(properties));
        return map;
    }

    @Override
    public void writeTo(PropertiesTxFileFormatConfig config, TranslationMap map, IOContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
