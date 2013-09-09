package net.txconsole.test;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public final class Helper {

    private Helper() {
    }

    public static String getResourceAsString(Class<?> root, String path) throws IOException {
        InputStream in = root.getResourceAsStream(path);
        if (in == null) {
            throw new IOException("Cannot find resource at " + path);
        } else {
            try {
                return IOUtils.toString(in, "UTF-8");
            } finally {
                in.close();
            }
        }
    }

}
