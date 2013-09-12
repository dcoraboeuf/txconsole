package net.txconsole.extension.svn;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.txconsole.core.model.VersionFormat;
import net.txconsole.extension.scm.AbstractSCMTxFileSource;
import net.txconsole.extension.svn.support.SVNService;
import net.txconsole.service.ScheduledService;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.IOContextFactory;
import net.txconsole.service.support.TxFileSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class SVNTxFileSource
        extends AbstractSCMTxFileSource<SVNTxFileSourceConfig>
        implements TxFileSource<SVNTxFileSourceConfig>,
        ScheduledService, Runnable {

    private final Logger logger = LoggerFactory.getLogger(SVNTxFileSource.class);
    private final IOContextFactory ioContextFactory;
    private final SVNService svnService;
    /**
     * Cache for contexts
     */
    private final Cache<String, Pair<IOContext, SVNTxFileSourceConfig>> contextCache = CacheBuilder
            .newBuilder()
            .maximumSize(10)
            .build();

    @Autowired
    public SVNTxFileSource(ObjectMapper objectMapper, IOContextFactory ioContextFactory, SVNService svnService) {
        super("extension-txfilesource-svn", "extension.svn.txfilesource", "extension.svn.txfilesource.description", SVNTxFileSourceConfig.class, objectMapper);
        this.ioContextFactory = ioContextFactory;
        this.svnService = svnService;
    }

    @Override
    public VersionFormat getVersionSemantics() {
        return new VersionFormat(
                "extension.svn.txfilesource.version",
                "\\d+"
        );
    }

    @Override
    public IOContext getSource(SVNTxFileSourceConfig config, String version) {
        // HEAD revision?
        boolean headRevision = StringUtils.isBlank(version);
        // Uses the cache for the head revisions
        if (headRevision) {
            return loadSourceFromCache(config);
        } else {
            // Gets a working directory
            IOContext context = ioContextFactory.createContext("svn");
            // Actual revision to use for the checkout
            SVNRevision revision = SVNRevision.parse(version);
            return checkout(config, context, revision);
        }
    }

    protected IOContext checkout(SVNTxFileSourceConfig config, IOContext context, SVNRevision revision) {
        // Checkout the files
        long lastRevision = svnService.checkout(
                context.getDir(),
                config.getUrl(),
                config.getUser(),
                config.getPassword(),
                revision
        );
        // TODO Does something with the last revision (it should be returned somehow in the IOContext...)
        // OK
        return context;
    }

    protected IOContext update(SVNTxFileSourceConfig config, IOContext context) {
        // Updates the files
        long lastRevision = svnService.update(
                context.getDir(),
                config.getUser(),
                config.getPassword()
        );
        // TODO Does something with the last revision (it should be returned somehow in the IOContext...)
        // OK
        return context;
    }

    private IOContext updateOrCheckout(SVNTxFileSourceConfig config, IOContext context, SVNRevision revision) {
        File wc = context.getDir();
        if (svnService.isWorkingCopy(wc)) {
            return update(config, context);
        } else {
            return checkout(config, context, revision);
        }
    }

    protected IOContext loadSourceFromCache(final SVNTxFileSourceConfig config) {
        // Gets the ID for this context (using a hash for the URL)
        final String key = getConfigKey(config);
        // Gets from the cache
        try {
            return contextCache.get(key, new Callable<Pair<IOContext, SVNTxFileSourceConfig>>() {
                @Override
                public Pair<IOContext, SVNTxFileSourceConfig> call() throws Exception {
                    return Pair.of(createOrSync(key, config), config);
                }
            }).getLeft();
        } catch (ExecutionException e) {
            throw new SVNTxFileSourceCannotGetLastVersionException(config.getUrl(), e);
        }
    }

    protected IOContext createOrSync(String key, SVNTxFileSourceConfig config) {
        // Gets the context for this key
        IOContext context = ioContextFactory.getOrCreateContext("svn", key);
        // Checks out or sync
        return updateOrCheckout(config, context, SVNRevision.HEAD);
    }

    protected String getConfigKey(SVNTxFileSourceConfig config) {
        return Sha512DigestUtils.shaHex(config.getUrl());
    }

    /**
     * Scans all entries in the cache and performs updates
     */
    @Override
    public void run() {
        logger.debug("[svntxfilesource] Sync start");
        Collection<Pair<IOContext, SVNTxFileSourceConfig>> values = contextCache.asMap().values();
        for (Pair<IOContext, SVNTxFileSourceConfig> value : values) {
            IOContext context = value.getLeft();
            SVNTxFileSourceConfig config = value.getRight();
            updateOrCheckout(config, context, SVNRevision.HEAD);
        }
        logger.debug("[svntxfilesource] Sync end");
    }

    @Override
    public Runnable getTask() {
        return this;
    }

    /**
     * Scans the cache every minute
     */
    @Override
    public Trigger getTrigger() {
        return new PeriodicTrigger(1, TimeUnit.MINUTES);
    }
}
