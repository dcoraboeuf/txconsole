package net.txconsole.extension.exchange.properties;

import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.txconsole.core.Content;
import net.txconsole.core.NamedContent;
import net.txconsole.core.model.*;
import net.txconsole.core.support.IOContextFactory;
import net.txconsole.extension.format.properties.PropertiesUtils;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.TxFileExchange;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class PropertiesTxFileExchange extends AbstractSimpleConfigurable<PropertiesTxFileExchangeConfig> implements TxFileExchange<PropertiesTxFileExchangeConfig> {

    public static final String ID = "extension-txfileexchange-properties";
    public static final String ENCODING = "UTF-8";
    private final IOContextFactory ioContextFactory;

    @Autowired
    public PropertiesTxFileExchange(ObjectMapper objectMapper, IOContextFactory ioContextFactory) {
        super(
                ID,
                "extension.txfileexchange.properties",
                "extension.txfileexchange.properties.description",
                PropertiesTxFileExchangeConfig.class,
                objectMapper);
        this.ioContextFactory = ioContextFactory;
    }

    @Override
    public Content export(PropertiesTxFileExchangeConfig configuration, Locale defaultLocale, Set<Locale> locales, TranslationDiff diff) {
        // Gets a working context
        File dir = ioContextFactory.createContext(ID).getDir();
        // Index of diff per bundle
        Map<String, List<TranslationDiffEntry>> index = new HashMap<>();
        // Indexing all the entries
        for (TranslationDiffEntry diffEntry : diff.getEntries()) {
            String bundle = diffEntry.getBundle();
            List<TranslationDiffEntry> list = index.get(bundle);
            if (list == null) {
                list = new ArrayList<>();
                index.put(bundle, list);
            }
            list.add(diffEntry);
        }
        // Generating all files
        for (String bundle : index.keySet()) {
            for (Locale locale : locales) {
                if (!defaultLocale.equals(locale)) {
                    export(dir, bundle, locale, index.get(bundle), defaultLocale);
                }
            }
        }
        // ZIP the folder
        File zip = zip(dir);
        // Returns the ZIP file
        try {
            return Content.of(zip, "application/zip");
        } catch (IOException ex) {
            throw new PropertiesTxFileExchangeIOException(zip.getName(), ex);
        }
    }

    @Override
    public TranslationMap read(PropertiesTxFileExchangeConfig configuration, Locale defaultLocale, Set<Locale> locales, NamedContent content) {
        // File name
        String fileName = content.getName();
        // TODO Checks the file is a property file
        // TODO In case this is a ZIP file, unzips the content of the file and treats each entry separately
        // Gets the bundle & locale from the file name
        Matcher m = Pattern.compile("(.*)_(.*)\\.properties").matcher(fileName);
        if (!m.matches()) {
            throw new PropertiesTxFileExchangeIncorrectFileNameException(fileName);
        }
        String bundle = m.group(1);
        Locale locale = new Locale(m.group(2));
        // Checks the locale
        if (!locales.contains(locale)) {
            throw new PropertiesTxFileExchangeUnsupportedLocale(locale);
        }
        // Reads the property file as UTF-8
        Map<String, String> properties;
        try {
            properties = PropertiesUtils.readProperties(new ByteArrayInputStream(content.getBytes()), ENCODING);
        } catch (IOException e) {
            throw new PropertiesTxFileExchangeIOException(fileName, e);
        }// Builder
        BundleBuilder bundleBuilder = BundleBuilder.create(bundle);
        BundleSectionBuilder sectionBuilder = bundleBuilder.getDefaultSectionBuilder();
        // For each property
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String label = entry.getValue();
            sectionBuilder.key(key).addValue(locale, label);
        }
        // OK
        return new TranslationMap(null, BundleCollectionBuilder.create().bundle(bundleBuilder.build()).build());
    }

    protected File zip(File dir) {
        try {
            // ZIP file to create
            File zip = new File(dir, "export.zip");
            // Opens the file for ZIP
            try (ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)))) {
                // Gets all files in the output directory
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        if (name.startsWith(".") || name.endsWith(".zip")) {
                            continue;
                        }
                        // Creates the ZIP entry
                        ZipEntry entry = new ZipEntry(name);
                        // Pushes it
                        zout.putNextEntry(entry);
                        try {
                            // Copies the file content
                            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                                IOUtils.copy(in, zout);
                            }
                        } finally {
                            zout.closeEntry();
                        }
                    }
                }
            }
            // OK
            return zip;
        } catch (IOException ex) {
            throw new PropertiesTxFileExchangeIOException("export.zip", ex);
        }
    }

    protected void export(File dir, String bundle, Locale targetLocale, List<TranslationDiffEntry> entries, Locale defaultLocale) {
        // File name
        String fileName = String.format("%s_%s.properties", bundle, targetLocale);
        // Target file
        File file = new File(dir, fileName);
        // Opens the file
        try {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING))) {
                // For all entries
                for (TranslationDiffEntry entry : entries) {
                    String key = entry.getKey();
                    // ADDED key
                    if (entry.getType() == TranslationDiffType.ADDED) {
                        String defaultValue = getNewValue(entry, defaultLocale);
                        if (StringUtils.isNotBlank(defaultValue)) {
                            String targetValue = getNewValue(entry, targetLocale);
                            if (StringUtils.isBlank(targetValue)) {
                                writer.format("# ADDED key%n");
                                for (TranslationDiffEntryValue translationDiffEntryValue : entry.getValues().values()) {
                                    String localeValue = translationDiffEntryValue.getNewValue();
                                    if (StringUtils.isNotBlank(localeValue)) {
                                        writer.format("# New value (%s): %s%n", translationDiffEntryValue.getLocale(), escapeForComment(localeValue));
                                    }
                                }
                                writer.format("%s = %s%n%n", key, defaultValue);
                            }
                        }
                    }
                    // UPDATED key
                    else {
                        String defaultValue = getNewValue(entry, defaultLocale);
                        String oldTarget = getOldValue(entry, targetLocale);
                        String newTarget = getNewValue(entry, targetLocale);
                        if (StringUtils.isNotBlank(defaultValue) && StringUtils.equals(oldTarget, newTarget)) {
                            writer.format("# UPDATED key%n");
                            // Diff in all languages
                            for (TranslationDiffEntryValue translationDiffEntryValue : entry.getValues().values()) {
                                Locale locale = translationDiffEntryValue.getLocale();
                                if (defaultLocale.equals(locale) || targetLocale.equals(locale)) {
                                    String oldLocaleValue = translationDiffEntryValue.getOldValue();
                                    String newLocaleValue = translationDiffEntryValue.getNewValue();
                                    if (StringUtils.isNotBlank(oldLocaleValue)) {
                                        writer.format("# Old value (%s): %s%n",
                                                locale,
                                                escapeForComment(oldLocaleValue)
                                        );
                                        if (!StringUtils.equals(oldLocaleValue, newLocaleValue)) {
                                            writer.format("# New value (%s): %s%n",
                                                    locale,
                                                    escapeForComment(newLocaleValue)
                                            );
                                        }
                                    }
                                }
                            }
                            // New value
                            writer.format("%s = %s%n%n", key, escapeForComment(defaultValue));
                        }
                    }
                    // Ignoring deleted keys
                }
            }
        } catch (IOException ex) {
            throw new PropertiesTxFileExchangeIOException(fileName, ex);
        }
    }

    protected String getNewValue(TranslationDiffEntry entry, Locale locale) {
        TranslationDiffEntryValue entryValue = entry.getValues().get(locale);
        if (entryValue != null) {
            return entryValue.getNewValue();
        } else {
            return null;
        }
    }

    protected String getOldValue(TranslationDiffEntry entry, Locale locale) {
        TranslationDiffEntryValue entryValue = entry.getValues().get(locale);
        if (entryValue != null) {
            return entryValue.getOldValue();
        } else {
            return null;
        }
    }

    private String escapeForComment(String message) {
        // Null values
        if (message == null) {
            return "";
        }

        // Replaces carriage returns by new lines with a leading comment entry
        message = StringUtils.replace(message, "\r", "\r# ");
        message = StringUtils.replace(message, "\n", " \n# ");

        // OK
        return message;
    }
}
