package net.txconsole.extension.svn;

import net.sf.jstring.support.CoreException;

public class SVNTxFileSourceSyncInterruptedException extends CoreException {
    public SVNTxFileSourceSyncInterruptedException(String url) {
        super(url);
    }
}
