package net.txconsole.service.support;

import net.txconsole.core.Content;
import net.txconsole.core.NamedContent;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationMap;

import java.util.Locale;
import java.util.Set;

/**
 * Defines the way to exchange translation files with the outside world. This interface
 * defines a way to 1) create the output file 2) read the returned file
 */
public interface TxFileExchange<C> extends Configurable<C> {

    Content export(C configuration, Locale defaultLocale, Set<Locale> locales, TranslationDiff diff);

    TranslationDiff read(C configuration, Locale defaultLocale, Set<Locale> locales, NamedContent content);

}
