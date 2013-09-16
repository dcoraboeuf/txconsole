package net.txconsole.extension.exchange.properties;

import net.txconsole.core.Content;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffEntry;
import net.txconsole.core.model.TranslationDiffType;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.IOContextFactory;
import net.txconsole.service.support.TxFileExchange;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class PropertiesTxFileExchange extends AbstractSimpleConfigurable<PropertiesTxFileExchangeConfig> implements TxFileExchange<PropertiesTxFileExchangeConfig> {

    public static final String ID = "extension-txfileexchange-properties";
    private final IOContextFactory ioContextFactory;
    private final EscapingService escapingService;

    @Autowired
    public PropertiesTxFileExchange(ObjectMapper objectMapper, IOContextFactory ioContextFactory, EscapingService escapingService) {
        super(
                ID,
                "extension.txfileexchange.properties",
                "extension.txfileexchange.properties.description",
                PropertiesTxFileExchangeConfig.class,
                objectMapper);
        this.ioContextFactory = ioContextFactory;
        this.escapingService = escapingService;
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
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
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
                                for (Map.Entry<Locale, Pair<String, String>> localeEntry : entry.getValues().entrySet()) {
                                    String localeValue = localeEntry.getValue().getRight();
                                    if (StringUtils.isNotBlank(localeValue)) {
                                        writer.format("# New value (%s): %s%n", localeEntry.getKey(), escapeForComment(localeValue));
                                    }
                                }
                                writer.format("%s = %s%n%n", key, escape(defaultValue));
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
                            for (Map.Entry<Locale, Pair<String, String>> localeEntry : entry.getValues().entrySet()) {
                                Locale locale = localeEntry.getKey();
                                if (defaultLocale.equals(locale) || targetLocale.equals(locale)) {
                                    String oldLocaleValue = getOldValue(entry, locale);
                                    String newLocaleValue = getNewValue(entry, locale);
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
        Pair<String, String> diff = entry.getValues().get(locale);
        if (diff != null) {
            return diff.getRight();
        } else {
            return null;
        }
    }

    protected String getOldValue(TranslationDiffEntry entry, Locale locale) {
        Pair<String, String> diff = entry.getValues().get(locale);
        if (diff != null) {
            return diff.getLeft();
        } else {
            return null;
        }
    }

    private String escapeForComment(String message) {
        // Null values
        if (message == null) {
            return "";
        }

        // Management of apostrophes
        message = escape(message);

        // Replaces carriage returns by new lines with a leading comment entry
        message = StringUtils.replace(message, "\r", "\r# ");
        message = StringUtils.replace(message, "\n", " \n# ");

        // OK
        return message;
    }

    protected String escape(String value) {
        return escapingService.escape(value);
    }
}
