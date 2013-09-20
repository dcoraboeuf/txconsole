package net.txconsole.extension.exchange.properties;

import net.txconsole.core.InputException;

import java.util.Locale;

public class PropertiesTxFileExchangeUnsupportedLocale extends InputException {
    public PropertiesTxFileExchangeUnsupportedLocale(Locale locale) {
        super(locale);
    }
}
