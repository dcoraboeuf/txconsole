package net.txconsole.core.support;

import net.sf.jstring.LocalizableMessage;

/**
 * {@link LocalizableMessage} that accepts only {@link String}s as parameters
 * and is therefore serializable as JSON without any ambiguity.
 */
public class SimpleMessage extends LocalizableMessage {
    /**
     * Constructor
     *
     * @param code       Key as an object
     * @param parameters List of parameters
     */
    public SimpleMessage(String code, String... parameters) {
        super(code, parameters);
    }
}
