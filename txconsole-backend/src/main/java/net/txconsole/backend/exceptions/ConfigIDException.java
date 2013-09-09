package net.txconsole.backend.exceptions;

import net.sf.jstring.LocalizableMessage;
import net.txconsole.core.InputException;

public class ConfigIDException extends InputException {

    public ConfigIDException(String configType, String id) {
        super(new LocalizableMessage(configType), id);
    }

}
