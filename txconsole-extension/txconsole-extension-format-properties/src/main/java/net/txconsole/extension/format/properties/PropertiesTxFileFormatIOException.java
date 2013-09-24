package net.txconsole.extension.format.properties;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class PropertiesTxFileFormatIOException extends CoreException {
    public PropertiesTxFileFormatIOException(String fileName, IOException e) {
        super(e, fileName);
    }
}
