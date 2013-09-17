package net.txconsole.core.support;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static java.lang.String.format;

public abstract class DirIOContextFactory implements IOContextFactory {

    @Override
    public IOContext createContext(String category) {
        return createContextWithName(format("%s-%s", category, UUID.randomUUID()));

    }

    @Override
    public IOContext getOrCreateContext(String category, String idInCategory) {
        return createContextWithName(format("%s-%s", category, idInCategory));
    }

    protected IOContext createContextWithName(String name) {
        // Directory
        File dir = new File(getRootDir(), name);
        // Creates it
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            throw new IOContextException(e);
        }
        // OK
        return new DirIOContext(dir);
    }

    protected abstract File getRootDir();
}
