package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;
import java.util.Map;

/**
 * Association of a {@link TranslationKey} and a list of labels
 * for the different languages.
 */
@Data
public class TranslationEntry {

    private final TranslationKey key;
    private final Map<Locale, String> labels;

}
