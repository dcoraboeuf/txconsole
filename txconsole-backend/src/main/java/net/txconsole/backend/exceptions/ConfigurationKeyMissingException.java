package net.txconsole.backend.exceptions;

import net.txconsole.service.model.ConfigurationKey;
import net.sf.jstring.support.CoreException;

public class ConfigurationKeyMissingException extends CoreException {

    public ConfigurationKeyMissingException(ConfigurationKey key) {
        super(key.name());
    }

}
