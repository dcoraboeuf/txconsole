package net.txconsole.backend.support;

import net.txconsole.backend.config.EnvironmentConfig;
import org.apache.commons.io.FileUtils;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HomeDirIOContextFactoryTest {

    @Test
    public void cleanup() throws IOException {
        // Configuration
        EnvironmentConfig config = mock(EnvironmentConfig.class);
        when(config.contextCleanupObsolescencePeriod()).thenReturn(Duration.standardHours(23));
        // Prepares the directory
        File root = new File("target/work/test/home");
        when(config.homeDir()).thenReturn(root);
        File contexts = new File(root, HomeDirIOContextFactory.CONTEXTS_DIR);
        // Directory content - old file
        File oldDir = new File(contexts, "old");
        FileUtils.forceMkdir(oldDir);
        oldDir.setLastModified(System.currentTimeMillis() - Period.days(1).toStandardSeconds().getSeconds() * 1000L);
        // Directory content - recent file
        File recentDir = new File(contexts, "recent");
        FileUtils.forceMkdir(recentDir);
        recentDir.setLastModified(System.currentTimeMillis() - Period.hours(22).toStandardSeconds().getSeconds() * 1000L);
        // Pre-checks
        assertTrue(oldDir.exists());
        assertTrue(recentDir.exists());
        // Factory to test
        HomeDirIOContextFactory factory = new HomeDirIOContextFactory(config);
        // Runs the cleanup
        factory.run();
        // Checks the directories
        assertFalse("Old dir must be deleted", oldDir.exists());
        assertTrue("Recent dir must be kept", recentDir.exists());
    }

}
