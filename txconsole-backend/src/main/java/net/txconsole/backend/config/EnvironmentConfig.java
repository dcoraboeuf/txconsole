package net.txconsole.backend.config;

import org.joda.time.Duration;

import java.io.File;

public interface EnvironmentConfig {

    File homeDir();

    /**
     * Interval of time between two clean-up operations
     */
    Duration contextCleanupScanInterval();

    /**
     * Period of validity for a context
     */
    Duration contextCleanupObsolescencePeriod();
}
