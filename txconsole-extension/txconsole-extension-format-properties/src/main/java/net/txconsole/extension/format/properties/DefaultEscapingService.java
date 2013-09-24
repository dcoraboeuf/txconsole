package net.txconsole.extension.format.properties;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DefaultEscapingService implements EscapingService {

    private static final Pattern SINGLE_APOS = Pattern.compile("[^']'[^']|^'[^']|[^']'$");

    /**
     * Replaces all single quotes by a double one
     */
    @Override
    public String escape(String value) {
        Matcher m = SINGLE_APOS.matcher(value);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, m.group().replace("'", "''"));
        }
        m.appendTail(b);
        value = b.toString();
        return value;
    }

}
