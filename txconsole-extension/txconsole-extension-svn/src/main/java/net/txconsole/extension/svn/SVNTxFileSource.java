package net.txconsole.extension.svn;

import net.txconsole.service.support.AbstractConfigurable;
import net.txconsole.service.support.FileSource;
import net.txconsole.service.support.TxFileSource;
import org.springframework.stereotype.Component;

// TODO Extends AbstractSCMTxFileSource (create a txconsole-extension-support module)
// TODO Defines the strings catalogue
@Component
public class SVNTxFileSource extends AbstractConfigurable<SVNTxFileSourceConfig> implements TxFileSource<SVNTxFileSourceConfig> {

    public SVNTxFileSource() {
        super("txfilesource-svn", "extension.svn.txfilesource", "extension.svn.txfilesource.description", SVNTxFileSourceConfig.class);
    }

    @Override
    public FileSource getSource() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
