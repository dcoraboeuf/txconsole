package net.txconsole.extension.svn;

import net.txconsole.extension.scm.AbstractSCMTxFileSource;
import net.txconsole.service.support.FileSource;
import net.txconsole.service.support.TxFileSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO Defines the strings catalogue
@Component
public class SVNTxFileSource extends AbstractSCMTxFileSource<SVNTxFileSourceConfig> implements TxFileSource<SVNTxFileSourceConfig> {


    @Autowired
    public SVNTxFileSource(ObjectMapper objectMapper) {
        super("extension-txfilesource-svn", "extension.svn.txfilesource", "extension.svn.txfilesource.description", SVNTxFileSourceConfig.class, objectMapper);
    }

    @Override
    public FileSource getSource(SVNTxFileSourceConfig config) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
