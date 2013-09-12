package net.txconsole.extension.svn;

import net.sf.jstring.support.CoreException;

public class SVNTxFileSourceLockTimeoutException extends CoreException {
    public SVNTxFileSourceLockTimeoutException(String url) {
        super(url);
    }
}
