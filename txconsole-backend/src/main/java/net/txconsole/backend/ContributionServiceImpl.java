package net.txconsole.backend;

import com.google.common.collect.Collections2;
import net.sf.jstring.LocalizableMessage;
import net.txconsole.backend.dao.ContributionDao;
import net.txconsole.core.model.Account;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.AccountService;
import net.txconsole.service.ContributionService;
import net.txconsole.service.MessageService;
import net.txconsole.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.concurrent.Callable;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final StructureService structureService;
    private final AccountService accountService;
    private final MessageService messageService;
    private final ContributionDao contributionDao;
    private final SecurityUtils securityUtils;

    @Autowired
    public ContributionServiceImpl(StructureService structureService, AccountService accountService, MessageService messageService, ContributionDao contributionDao, SecurityUtils securityUtils) {
        this.structureService = structureService;
        this.accountService = accountService;
        this.messageService = messageService;
        this.contributionDao = contributionDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional
    public LocalizableMessage post(int branchId, ContributionInput input) {
        // Branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getProjectId());
        // Contribution mode
        boolean direct = securityUtils.isGranted(ProjectFunction.CONTRIBUTION_DIRECT, branch.getProjectId());
        // FIXME Direct mode
        if (direct) {
            throw new RuntimeException("NYI");
        }
        // Staging
        else {
            return stage(branchId, input);
        }
    }

    protected LocalizableMessage stage(int branchId, ContributionInput input) {
        // Branch
        BranchSummary branch = structureService.getBranch(branchId);
        final int projectId = branch.getProjectId();
        // Gets the current account
        Account account = securityUtils.getCurrentAccount();
        // Saves the contribution
        contributionDao.post(account.getId(), branchId, input);
        // Sends a message to all the reviewers for the project
        // Gets the addresses of all reviewers for this project
        Collection<String> addresses = securityUtils.asAdmin(new Callable<Collection<String>>() {
            @Override
            public Collection<String> call() throws Exception {
                return Collections2.transform(
                        accountService.findAccountsForProjectACL(projectId, ProjectFunction.CONTRIBUTION_REVIEW),
                        Account.emailFn
                );
            }
        });
        // TODO Generates a message (English only)
        // TODO Sends the message
        // messageService.sendMessage(message, new MessageDestination(MessageChannel.EMAIL, addresses));
        // OK
        return new LocalizableMessage("contribution.staged");
    }

}
