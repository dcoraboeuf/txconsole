package net.txconsole.extension.svn.support;

import net.sf.jstring.support.CoreException;
import org.tmatesoft.svn.core.SVNException;

public class CoreSVNException extends CoreException {
    public CoreSVNException(SVNException e) {
        super(e);
    }
}
