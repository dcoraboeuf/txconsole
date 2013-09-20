package net.txconsole.extension.format.properties;

import com.google.common.collect.ImmutableMap;
import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.support.IOContext;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.TxFileFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static net.txconsole.extension.format.properties.PropertiesUtils.readProperties;

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
            Map<String, String> map = readProperties(file, "UTF-8");
            // Ok
            return ImmutableMap.copyOf(map);
        }
    }

    @Override
    public void writeTo(PropertiesTxFileFormatConfig config, TranslationMap map, IOContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
