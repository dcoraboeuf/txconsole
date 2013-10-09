package net.txconsole.backend.support;

import net.txconsole.backend.config.EnvironmentConfig;
import net.txconsole.core.support.DirIOContextFactory;
import net.txconsole.service.ScheduledService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class HomeDirIOContextFactory extends DirIOContextFactory implements ScheduledService, Runnable {

    public static final String CONTEXTS_DIR = "contexts";
    private final Logger logger = LoggerFactory.getLogger(HomeDirIOContextFactory.class);
    private final EnvironmentConfig environmentConfig;

    @Autowired
    public HomeDirIOContextFactory(EnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    @Override
    protected File getRootDir() {
        return new File(environmentConfig.homeDir(), CONTEXTS_DIR);
    }

    @Override
    public void run() {
        logger.debug("[homedirio] Scanning for obsolete contexts...");
        // FIXME Scan for obsolete contexts
    }

    /**
     * The interval may be configured at runtime.
     */
    @Override
    public Trigger getTrigger() {
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                DateTime now = DateTime.now();
                DateTime future = now.plus(environmentConfig.contextCleanupScanInterval());
                return future.toDate();
            }
        };
    }

    @Override
    public Runnable getTask() {
        return this;
    }
}
