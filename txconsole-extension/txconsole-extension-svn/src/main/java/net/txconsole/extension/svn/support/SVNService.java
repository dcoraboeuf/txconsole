package net.txconsole.extension.svn.support;

import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;

public interface SVNService {

    void checkout(File dir, String url, String user, String password, SVNRevision revision);

}
