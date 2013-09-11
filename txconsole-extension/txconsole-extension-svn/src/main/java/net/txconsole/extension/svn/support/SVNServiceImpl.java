package net.txconsole.extension.svn.support;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import java.io.File;

@Service
public class SVNServiceImpl implements SVNService {

    @Override
    public void checkout(File dir, String url, String user, String password, SVNRevision revision) {
        SvnOperationFactory factory = new SvnOperationFactory();
        try {
            factory.setAuthenticationManager(new BasicAuthenticationManager(user, password));
            SvnCheckout co = factory.createCheckout();
            co.setSource(SvnTarget.fromURL(SVNURL.parseURIEncoded(url)));
            co.setRevision(revision);
            co.setSingleTarget(SvnTarget.fromFile(dir));
        } catch (SVNException e) {
            throw new CoreSVNException(e);
        } finally {
            factory.dispose();
        }
    }

}
