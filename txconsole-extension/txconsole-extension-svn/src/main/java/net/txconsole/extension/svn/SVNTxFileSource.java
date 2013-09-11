package net.txconsole.extension.svn;

import net.txconsole.extension.scm.AbstractSCMTxFileSource;
import net.txconsole.extension.svn.support.SVNService;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.IOContextFactory;
import net.txconsole.service.support.TxFileSource;
import net.txconsole.core.model.VersionFormat;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.wc.SVNRevision;

@Component
public class SVNTxFileSource extends AbstractSCMTxFileSource<SVNTxFileSourceConfig> implements TxFileSource<SVNTxFileSourceConfig> {

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

    @Override
    public IOContext getSource(SVNTxFileSourceConfig config, String version) {
        // Revision
        SVNRevision revision = StringUtils.isNotBlank(version) ? SVNRevision.parse(version) : SVNRevision.HEAD;
        // Gets a working directory
        IOContext context = ioContextFactory.createContext("svn");
        // Checkout the files
        long lastRevision = svnService.checkout(
                context.getDir(),
                config.getUrl(),
                config.getUser(),
                config.getPassword(),
                revision
        );
        // TODO Does something with the last revision (it should be returned somehow in the IOContext...)
        // Returns a directory context
        return context;
    }
}
