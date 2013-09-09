package net.txconsole.service.support;

import java.util.Collection;

public interface TranslationSourceService {

    /**
     * Gets the list of translation sources
     */
    Collection<TranslationSource<?>> getTranslationSourceList();

    /**
     * Gets the list of translation file sources
     */
    Collection<TxFileSource<?>> getTxFileSourceList();

    /**
     * Gets the list of translation file format
     */
    Collection<TxFileFormat<?>> getTxFileFormatList();

}
