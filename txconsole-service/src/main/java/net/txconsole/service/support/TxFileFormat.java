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
     * Reads the map for the file context
     */
    TranslationMap readFrom(C config, IOContext context);

    /**
     * Writes the map into the file context
     */
    void writeTo(C config, TranslationMap map, IOContext context);

}
