package net.txconsole.backend.config;

import org.joda.time.Duration;

/**
 * Reasonable defaults
 */
public abstract class AbstractEnvironmentConfig implements EnvironmentConfig {

    /**
     * Every hour.
     */
    @Override
    public Duration contextCleanupScanInterval() {
        return Duration.standardHours(1);
    }

    /**
     * One day at most.
     */
    @Override
    public Duration contextCleanupObsolescencePeriod() {
        return Duration.standardDays(1);
    }
}
