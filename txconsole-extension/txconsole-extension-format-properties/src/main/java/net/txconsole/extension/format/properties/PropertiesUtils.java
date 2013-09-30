package net.txconsole.extension.format.properties;

import com.google.common.collect.Maps;
import net.txconsole.core.support.UnicodeReader;
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

    public static Map<String, String> readProperties(File file, String encoding) {
        try {
            try (FileInputStream in = new FileInputStream(file)) {
                return readProperties(in, encoding);
            }
        } catch (IOException ex) {
            throw new PropertyFileCannotReadException(file.getName(), ex);
        }
    }

    public static Map<String, String> readProperties(InputStream input, String encoding) throws IOException {
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
        map.putAll(Maps.fromProperties(properties));
        return map;
    }

    public static void writeProperties(OutputStream fout,
                                       Map<String, String> map) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(fout), "US-ASCII"));
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                out.format("%s = %s\n", key, escapePropertyValue(value));
            }
        } finally {
            out.flush();
        }
    }

    public static String escapePropertyValue(String value) {
        String result = StringEscapeUtils.escapeJava(value);
        if (" ".equals(result)) {
            result = "\\ ";
        }
        return result;
    }
}