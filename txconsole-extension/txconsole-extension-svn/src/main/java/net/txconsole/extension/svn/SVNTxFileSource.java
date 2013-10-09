package net.txconsole.extension.svn;

import com.google.common.base.Function;
import net.txconsole.core.model.VersionFormat;
import net.txconsole.core.support.IOContext;
import net.txconsole.core.support.IOContextFactory;
import net.txconsole.extension.scm.AbstractSCMTxFileSource;
import net.txconsole.extension.svn.support.SVNService;
import net.txconsole.service.support.TxFileSource;
import net.txconsole.service.support.TxFileSourceResult;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.wc.SVNRevision;

@Component
public class SVNTxFileSource
        extends AbstractSCMTxFileSource<SVNTxFileSourceConfig>
        implements TxFileSource<SVNTxFileSourceConfig> {

    /**
     * Set this system property to disable commits: {@value}.
     */
    public static final String TXCONSOLE_EXTENSION_SVN_NOCOMMIT = "txconsole.extension.svn.nocommit";
    private final IOContextFactory ioContextFactory;
    private final SVNService svnService;

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

    protected IOContext getSource(SVNTxFileSourceConfig config, String version) {
        // Gets a working directory
        IOContext context = ioContextFactory.createContext("svn");
        // Actual revision to use for the checkout
        SVNRevision revision = SVNRevision.parse(version);
        // Checks out
        return checkout(config, context, revision);
    }

    @Override
    public <T> TxFileSourceResult<T> withReadableSource(SVNTxFileSourceConfig config, String version, Function<IOContext, T> action) {
        // Gets the context for this version
        IOContext context = getSource(config, version);
        // Executes the action
        T result = action.apply(context);
        // OK
        return new TxFileSourceResult<>(context.getVersion(), result);
    }

    @Override
    public <T> TxFileSourceResult<T> withWritableSource(SVNTxFileSourceConfig config, String version, String message, Function<IOContext, T> action) {
        // Gets the context for this version
        IOContext context = getSource(config, version);
        // Executes the action
        T result = action.apply(context);
        // Commits the resulting context using the given message
        long revision = -1;
        if (!Boolean.getBoolean(TXCONSOLE_EXTENSION_SVN_NOCOMMIT)) {
            revision = svnService.commit(context.getDir(), message, config.getUser(), config.getPassword());
        }
        // OK
        return new TxFileSourceResult<>(String.valueOf(revision), result);
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
        // OK
        return context.withVersion(String.valueOf(lastRevision));
    }
}
