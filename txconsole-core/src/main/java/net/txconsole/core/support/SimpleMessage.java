package net.txconsole.core.support;

import net.sf.jstring.LocalizableMessage;
import org.apache.commons.lang3.ObjectUtils;

/**
 * {@link LocalizableMessage} that accepts only {@link String}s as parameters
 * and is therefore serializable as JSON without any ambiguity.
 */
public class SimpleMessage extends LocalizableMessage {

    private final String code;
    private final String[] parameters;

    /**
     * Constructor
     *
     * @param code       Key as an object
     * @param parameters List of parameters
     */
    public SimpleMessage(String code, String... parameters) {
        super(code, parameters);
        this.code = code;
        this.parameters = parameters;
    }

    public SimpleMessage(String code, Object... parameters) {
        this(code, toStrings(parameters));
    }

    public String getCode() {
        return code;
    }

    public String[] getParameters() {
        return parameters;
    }

    private static String[] toStrings(Object[] parameters) {
        String[] strings = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object o = parameters[i];
            strings[i] = ObjectUtils.toString(o, "");
        }
        return strings;
    }
}
