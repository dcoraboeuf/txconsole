package net.txconsole.extension.svn.support;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.*;

import java.io.File;

@Service
public class SVNServiceImpl implements SVNService {

    private final Logger logger = LoggerFactory.getLogger(SVNService.class);

    @Override
    public long checkout(File dir, String url, String user, String password, SVNRevision revision) {
        logger.debug("[svn] CO {}@{} in {}", url, revision, dir);
        try (SVNOp ops = ops(user, password)) {
            SvnCheckout co = ops.getOperationFactory().createCheckout();
            co.setSource(SvnTarget.fromURL(SVNURL.parseURIEncoded(url)));
            co.setRevision(revision);
            co.setSingleTarget(SvnTarget.fromFile(dir));
            return co.run();
        } catch (SVNException ex) {
            throw new CoreSVNException(ex);
        }
    }

    @Override
    public long update(File dir, String user, String password) {
        logger.debug("[svn] UP in {}", dir);
        try (SVNOp ops = ops(user, password)) {
            SvnUpdate up = ops.getOperationFactory().createUpdate();
            up.setSingleTarget(SvnTarget.fromFile(dir));
            return up.run()[0];
        } catch (SVNException ex) {
            throw new CoreSVNException(ex);
        }
    }

    @Override
    public boolean isWorkingCopy(File wc) {
        return SvnOperationFactory.isVersionedDirectory(wc);
    }

    @Override
    public long commit(File dir, String message, String user, String password) {
        logger.debug("[svn] CI in {}", dir);
        try (SVNOp ops = ops(user, password)) {
            SvnCommit ci = ops.getOperationFactory().createCommit();
            ci.setSingleTarget(SvnTarget.fromFile(dir));
            ci.setCommitMessage(message);
            SVNCommitInfo info = ci.run();
            return info.getNewRevision();
        } catch (SVNException ex) {
            throw new CoreSVNException(ex);
        }
    }

    protected SVNOp ops(String user, String password) {
        SvnOperationFactory factory = new SvnOperationFactory();
        factory.setAuthenticationManager(new BasicAuthenticationManager(user, password));
        return new SVNOp(factory);
    }

    @Data
    private static final class SVNOp implements AutoCloseable {

        private final SvnOperationFactory operationFactory;

        @Override
        public void close() {
            operationFactory.dispose();
        }
    }
}
