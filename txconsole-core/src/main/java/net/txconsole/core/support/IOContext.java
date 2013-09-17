package net.txconsole.core.support;

import java.io.File;

/**
 * Defines an abstraction to access files, without accessing
 * Java IO directly.
 */
public interface IOContext {

    /**
     * Version for this context
     */
    String getVersion();

    /**
     * Sets the version for this content and returns this context
     */
    IOContext withVersion(String version);

    /**
     * Gets the root directory
     */
    File getDir();

    /**
     * Gets a file from the context
     */
    File getFile(String fileName);
}
