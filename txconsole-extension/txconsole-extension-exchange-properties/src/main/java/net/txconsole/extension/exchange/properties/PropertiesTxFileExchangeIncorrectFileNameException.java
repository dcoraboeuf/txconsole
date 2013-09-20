package net.txconsole.extension.exchange.properties;

import net.txconsole.core.InputException;

public class PropertiesTxFileExchangeIncorrectFileNameException extends InputException {
    public PropertiesTxFileExchangeIncorrectFileNameException(String fileName) {
        super(fileName);
    }
}
