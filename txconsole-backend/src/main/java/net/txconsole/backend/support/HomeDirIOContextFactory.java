package net.txconsole.backend.support;

import net.txconsole.backend.config.EnvironmentConfig;
import net.txconsole.core.support.DirIOContextFactory;
import net.txconsole.service.ScheduledService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
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
        DateTime now = DateTime.now();
        Date cutoffDate = now.minus(environmentConfig.contextCleanupObsolescencePeriod()).toDate();
        // Gets the list of all directories in the root dir that are older that the obsolescence period
        File[] dirs = getRootDir().listFiles(
                (FileFilter) new AndFileFilter(
                        DirectoryFileFilter.INSTANCE,
                        new AgeFileFilter(cutoffDate)
                )
        );
        if (dirs != null) {
            for (File dir : dirs) {
                // Deletes the directory
                logger.debug("[homedirio] Removing old directory: {}", dir);
                try {
                    FileUtils.forceDelete(dir);
                } catch (IOException ex) {
                    logger.error(String.format("[homedirio] Error while removing directory: %s", dir), ex);
                }
            }
        }
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
                logger.debug("[homedirio] Next scan at {}", future);
                return future.toDate();
            }
        };
    }

    @Override
    public Runnable getTask() {
        return this;
    }
}
