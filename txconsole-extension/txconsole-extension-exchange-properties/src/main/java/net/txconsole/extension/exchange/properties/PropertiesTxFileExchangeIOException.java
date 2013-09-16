package net.txconsole.extension.exchange.properties;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class PropertiesTxFileExchangeIOException extends CoreException {
    public PropertiesTxFileExchangeIOException(String fileName, IOException ex) {
        super(ex, fileName);
    }
}
