package net.txconsole.extension.svn.support;

import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;

public interface SVNService {

    long checkout(File dir, String url, String user, String password, SVNRevision revision);

    long update(File dir, String user, String password);

    boolean isWorkingCopy(File wc);
}
