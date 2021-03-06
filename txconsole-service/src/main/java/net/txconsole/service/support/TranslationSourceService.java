package net.txconsole.service.support;

import net.txconsole.core.model.JsonConfiguration;

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

    /**
     * Gets the list of translation file exchanges
     */
    Collection<TxFileExchange<?>> getTxFileExchangeList();

    /**
     * Gets an actual configuration using an input configuration
     */
    <C> Configured<C, TranslationSource<C>> getConfiguredTranslationSource(JsonConfiguration config);

    <S> Configured<S, TxFileSource<S>> getConfiguredTxFileSource(JsonConfiguration config);

    <F> Configured<F, TxFileFormat<F>> getConfiguredTxFileFormat(JsonConfiguration config);

    <F> Configured<F, TxFileExchange<F>> getConfiguredTxFileExchange(JsonConfiguration config);
}
