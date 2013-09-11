package net.txconsole.backend.support;

import net.txconsole.service.support.IOContext;

import java.io.File;

// FIXME Scheduled service to delete old directories
public class DirIOContext implements IOContext {

    private final File dir;

    public DirIOContext(File dir) {
        this.dir = dir;
    }

    @Override
    public File getDir() {
        return dir;
    }

    @Override
    public File getFile(String fileName) {
        return new File(dir, fileName);
    }
}
