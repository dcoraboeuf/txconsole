package net.txconsole.extension.format.properties;

import net.sf.jstring.support.CoreException;

public class PropertyFileNotFoundException extends CoreException {
    public PropertyFileNotFoundException(String fileName) {
        super(fileName);
    }
}
