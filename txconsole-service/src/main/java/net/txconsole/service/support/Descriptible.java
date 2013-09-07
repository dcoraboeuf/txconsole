package net.txconsole.service.support;

/**
 * Defines an object that can be described.
 */
public interface Descriptible {

    /**
     * Returns a unique ID for this object type
     */
    String getId();

    /**
     * Returns its name as a localization key
     */
    String getNameKey();

    /**
     * Returns its description as a localization key
     */
    String getDescriptionKey();

}
