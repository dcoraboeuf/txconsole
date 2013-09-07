package net.txconsole.service.support;

import java.util.Collection;

public interface TranslationSourceService {

    /**
     * Gets the list of translation sources
     */
    Collection<TranslationSource<?>> getTranslationSourceList();

}
