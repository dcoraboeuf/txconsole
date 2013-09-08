package net.txconsole.service.support;

import net.txconsole.core.model.TranslationMap;

/**
 * Defines the protocol needed for accessing the files and transform them back and forth
 * into translation maps:
 *
 * @param <C> Type of configuration
 */
public interface TxFileFormat<C> extends Configurable<C> {

    /**
     * Reads the map for the file source
     */
    TranslationMap readFrom(C config, FileSource source);

    /**
     * Writes the map into the file source
     */
    void writeTo(C config, TranslationMap map, FileSource source);

}
