package net.txconsole.extension.format.properties;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class PropertyFileCannotReadException extends CoreException {
    public PropertyFileCannotReadException(String name, IOException ex) {
        super(ex, name);
    }
}
