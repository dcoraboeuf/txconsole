package net.txconsole.core.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffControl {

    private final int entryId;
    private final String bundle;
    private final String section;
    private final String key;
    private final Map<Locale, String> messages = new HashMap<>();

    public TranslationDiffControl add(Locale locale, String message) {
        messages.put(locale, message);
        return this;
    }

}
