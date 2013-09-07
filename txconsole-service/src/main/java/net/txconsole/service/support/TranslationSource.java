package net.txconsole.service.support;

import net.txconsole.core.model.TranslationMap;

/**
 * Provides access to a {@link net.txconsole.core.model.TranslationMap},
 * in writing or in reading.
 */
public interface TranslationSource<C> extends Configurable<C> {

    /**
     * Reads the translation map using the current configuration.
     */
    TranslationMap read();

    /**
     * Writes the translation map using the current configuration.
     */
    void write(TranslationMap map);

}
