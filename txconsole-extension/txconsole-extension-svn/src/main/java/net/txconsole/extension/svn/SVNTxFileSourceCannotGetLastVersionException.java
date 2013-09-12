package net.txconsole.extension.svn;

import net.sf.jstring.support.CoreException;

import java.util.concurrent.ExecutionException;

public class SVNTxFileSourceCannotGetLastVersionException extends CoreException {
    public SVNTxFileSourceCannotGetLastVersionException(String url, ExecutionException e) {
        super(e, url);
    }
}
