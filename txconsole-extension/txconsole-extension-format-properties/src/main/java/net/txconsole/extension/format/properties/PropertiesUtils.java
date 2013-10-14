package net.txconsole.extension.format.properties;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import net.txconsole.core.support.UnicodeReader;
import net.txconsole.service.EscapingService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public final class PropertiesUtils {

    private PropertiesUtils() {
    }

    public static Map<String, String> readProperties(File file, String encoding, EscapingService escapingService) {
        try {
            try (FileInputStream in = new FileInputStream(file)) {
                return readProperties(in, encoding, escapingService);
            }
        } catch (IOException ex) {
            throw new PropertyFileCannotReadException(file.getName(), ex);
        }
    }

    public static Map<String, String> readProperties(InputStream input, String encoding, final EscapingService escapingService) throws IOException {
        BufferedReader in = new BufferedReader(new UnicodeReader(input, encoding));
        Properties properties = new Properties();
        properties.load(in);
        Map<String, String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (StringUtils.equalsIgnoreCase(o1, o2)) {
                    return o1.compareTo(o2);
                } else {
                    return o1.compareToIgnoreCase(o2);
                }
            }
        });
        map.putAll(
                Maps.transformValues(
                        Maps.fromProperties(properties),
                        new Function<String, String>() {
                            @Override
                            public String apply(String value) {
                                return escapingService.read(value);
                            }
                        }
                )
        );
        return map;
    }

    public static void writeProperties(OutputStream fout,
                                       Map<String, String> map,
                                       EscapingService escapingService) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(fout), "US-ASCII"));
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                out.format("%s = %s\n", key, escapePropertyValue(value, escapingService));
            }
        } finally {
            out.flush();
        }
    }

    public static String escapePropertyValue(String value, EscapingService escapingService) {
        String result = StringEscapeUtils.escapeJava(escapingService.write(value));
        if (" ".equals(result)) {
            return "\\ ";
        } else {
            return result;
        }
    }
}
