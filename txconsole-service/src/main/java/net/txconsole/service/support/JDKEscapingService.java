package net.txconsole.service.support;

import net.txconsole.service.EscapingService;
import org.apache.commons.lang3.StringUtils;

/**
 * Escapes toward property files compatible with {@link java.text.MessageFormat} from the JDK.
 */
public class JDKEscapingService implements EscapingService {

    /**
     * Replaces all single quotes by a double one
     */
    @Override
    public String write(String value) {
        return StringUtils.replace(value, "'", "''");
    }

    /**
     * Replaces the double apostrophes by single ones.
     */
    @Override
    public String read(String value) {
        return StringUtils.replace(value, "''", "'");
    }

}
