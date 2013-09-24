package net.txconsole.core.model;

import lombok.Data;
import net.sf.jstring.support.KeyIdentifier;

import java.util.Locale;
import java.util.Map;

/**
 * Association of a {@link KeyIdentifier} and a list of labels
 * for the different languages.
 */
@Data
public class TranslationEntry {

    private final KeyIdentifier key;
    private final Map<Locale, String> labels;

}
