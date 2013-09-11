package net.txconsole.service.support;

import java.io.File;

/**
 * Defines an abstraction to access files, without accessing
 * Java IO directly.
 */
public interface IOContext {

    /**
     * Gets the root directory
     */
    File getDir();

    /**
     * Gets a file from the context
     */
    File getFile(String fileName);
}
