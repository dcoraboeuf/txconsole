package net.txconsole.service.support;

import net.txconsole.service.EscapingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Escapes toward property files compatible with {@link java.text.MessageFormat} from the JDK.
 */
@Component
public class JDKEscapingService implements EscapingService {

    private static final Pattern SINGLE_APOS = Pattern.compile("[^']'[^']|^'[^']|[^']'$");

    /**
     * Replaces all single quotes by a double one
     */
    @Override
    public String escapeForStorage(String value) {
        Matcher m = SINGLE_APOS.matcher(value);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, m.group().replace("'", "''"));
        }
        m.appendTail(b);
        value = b.toString();
        return value;
    }

    /**
     * Replaces the double apostrophes by single ones.
     */
    @Override
    public String escapeForEdition(String value) {
        return StringUtils.replace(value, "''", "'");
    }

}
