package net.txconsole.extension.svn;

import net.txconsole.extension.scm.AbstractSCMTxFileSource;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.TxFileSource;
import net.txconsole.core.model.VersionFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNTxFileSource extends AbstractSCMTxFileSource<SVNTxFileSourceConfig> implements TxFileSource<SVNTxFileSourceConfig> {


    @Autowired
    public SVNTxFileSource(ObjectMapper objectMapper) {
        super("extension-txfilesource-svn", "extension.svn.txfilesource", "extension.svn.txfilesource.description", SVNTxFileSourceConfig.class, objectMapper);
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
