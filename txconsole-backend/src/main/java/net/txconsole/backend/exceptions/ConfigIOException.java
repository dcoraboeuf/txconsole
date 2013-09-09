package net.txconsole.backend.exceptions;

import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class ConfigIOException extends CoreException {
    public ConfigIOException(String configType, String id, IOException e) {
        super(e, new LocalizableMessage(configType), id, e);
    }
}
