package net.txconsole.core;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Data
public class Content {

    private final String type;
    private final byte[] bytes;

    public static Content of(File file, String type) throws IOException {
        return new Content(
                type,
                FileUtils.readFileToByteArray(file)
        );
    }
}
