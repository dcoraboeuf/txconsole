package net.txconsole.core.model;

import lombok.Data;

/**
 * Defines the format and the label to use for the concept of a version.
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
