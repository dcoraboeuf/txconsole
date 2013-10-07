package net.txconsole.backend;

import com.google.common.collect.Collections2;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.Strings;
import net.txconsole.backend.dao.ContributionDao;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final StructureService structureService;
    private final AccountService accountService;
    private final MessageService messageService;
    private final TemplateService templateService;
    private final ContributionDao contributionDao;
    private final SecurityUtils securityUtils;
    private final Strings strings;

    @Autowired
    public ContributionServiceImpl(StructureService structureService, AccountService accountService, MessageService messageService, TemplateService templateService, ContributionDao contributionDao, SecurityUtils securityUtils, Strings strings) {
        this.structureService = structureService;
        this.accountService = accountService;
        this.messageService = messageService;
        this.templateService = templateService;
        this.contributionDao = contributionDao;
        this.securityUtils = securityUtils;
        this.strings = strings;
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
        ProjectSummary project = structureService.getProject(projectId);
        // Gets the current account
        Account account = securityUtils.getCurrentAccount();
        // Saves the contribution
        int contributionId = contributionDao.post(account.getId(), branchId, input);
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
        // Generates a message (English only)
        Locale locale = Locale.ENGLISH;
        String title = strings.get(locale, "contribution.staged.title", contributionId, account.getFullName(), branch.getName(), project.getName());
        TemplateModel model = new TemplateModel();
        model.add("account", account.getFullName());
        model.add("title", title);
        model.add("contribution", contributionId);
        model.add("branch", branch);
        model.add("project", project);
        String content = templateService.generate("contribution-staged.html", Locale.ENGLISH, model);
        // Sends the message
        Message message = new Message(
                title,
                new MessageContent(
                        MessageContentType.HTML,
                        content
                )
        );
        messageService.sendMessage(message, new MessageDestination(MessageChannel.EMAIL, addresses));
        // OK
        return new LocalizableMessage("contribution.staged");
    }

}
