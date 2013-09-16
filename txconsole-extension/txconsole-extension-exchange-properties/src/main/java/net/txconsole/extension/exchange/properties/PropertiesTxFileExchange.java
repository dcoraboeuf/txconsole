package net.txconsole.extension.exchange.properties;

import net.txconsole.core.Content;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffEntry;
import net.txconsole.core.model.TranslationDiffType;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.IOContextFactory;
import net.txconsole.service.support.TxFileExchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

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
        // TODO ZIP the folder
        // TODO Returns the ZIP file
        return null;
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
                        Pair<String, String> defaultDiff = entry.getValues().get(defaultLocale);
                        if (defaultDiff != null) {
                            writer.format("# ADDED key%n");
                            for (Map.Entry<Locale, Pair<String, String>> localeEntry : entry.getValues().entrySet()) {
                                writer.format("# New value (%s): %s%n", localeEntry.getKey(), escapeForComment(localeEntry.getValue().getRight()));
                            }
                            writer.format("%s = %s%n", key, escape(defaultDiff.getRight()));
                        }
                    }
                    // UPDATED key
                    else {
                        Pair<String, String> defaultDiff = entry.getValues().get(defaultLocale);
                        Pair<String, String> targetLocaleDiff = entry.getValues().get(targetLocale);
                        if (defaultDiff != null) {
                            writer.format("# UPDATED key%n");
                            // Old value in this language (if any)
                            if (targetLocaleDiff != null && targetLocaleDiff.getRight() != null) {
                                writer.format("# Old value (%s): %s%n",
                                        targetLocale,
                                        escapeForComment(targetLocaleDiff.getRight())
                                );
                            }
                            // Old default value
                            writer.format("# Old value (%s): %s%n",
                                    defaultLocale,
                                    escapeForComment(defaultDiff.getLeft())
                            );
                            writer.format("# New value (%s): %s%n",
                                    defaultLocale,
                                    escapeForComment(defaultDiff.getRight())
                            );
                            writer.format("%s = %s%n", key, escapeForComment(defaultDiff.getRight()));
                        }
                    }
                    // Ignoring deleted keys
                }
            }
        } catch (IOException ex) {
            throw new PropertiesTxFileExchangeIOException(fileName, ex);
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
