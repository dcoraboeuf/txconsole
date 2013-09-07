package net.txconsole.service.support;

/**
 * Provides access to a {@link net.txconsole.core.model.TranslationMap},
 * in writing or in reading.
 */
public interface TranslationSource<C> {

    /**
     *
     * @return
     */
    Class<C> getConfigClass();

}
