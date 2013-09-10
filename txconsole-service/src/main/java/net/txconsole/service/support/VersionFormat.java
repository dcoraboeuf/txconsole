package net.txconsole.service.support;

import lombok.Data;

/**
 * Defines the format and the label to use for the concept of a version
 * in a {@link TranslationSource}.
 *
 * @see net.txconsole.service.support.TranslationSource#getVersionSemantics()
 */
@Data
public class VersionFormat {

    /**
     * The localization key attached to the version
     */
    private final String semanticKey;
    /**
     * The {@link java.util.regex.Pattern Regex} pattern used to validate
     * a version.
     */
    private final String validationPattern;

}
