package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.Strings;
import net.txconsole.backend.dao.ContributionDao;
import net.txconsole.backend.dao.model.TContribution;
import net.txconsole.backend.dao.model.TContributionDetail;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.core.support.TimeUtils;
import net.txconsole.service.*;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final StructureService structureService;
    private final AccountService accountService;
    private final ResourceService resourceService;
    private final MessageService messageService;
    private final TemplateService templateService;
    private final ContributionDao contributionDao;
    private final SecurityUtils securityUtils;
    private final Strings strings;
    private final Function<TContribution, ContributionSummary> contributionSummaryFunction = new Function<TContribution, ContributionSummary>() {

        @Override
        public ContributionSummary apply(TContribution t) {
            return new ContributionSummary(
                    t.getId(),
                    true,
                    t.getBranch(),
                    t.getMessage(),
                    accountService.getAccountSummary(t.getAuthor()),
                    t.getTimestamp()
            );
        }
    };

    @Autowired
    public ContributionServiceImpl(StructureService structureService, AccountService accountService, ResourceService resourceService, MessageService messageService, TemplateService templateService, ContributionDao contributionDao, SecurityUtils securityUtils, Strings strings) {
        this.structureService = structureService;
        this.accountService = accountService;
        this.resourceService = resourceService;
        this.messageService = messageService;
        this.templateService = templateService;
        this.contributionDao = contributionDao;
        this.securityUtils = securityUtils;
        this.strings = strings;
    }

    @Override
    @Transactional(readOnly = true)
    public ContributionSummary getContribution(int id) {
        return contributionSummaryFunction.apply(contributionDao.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionDetail> getContributionDetails(int id) {
        return Lists.transform(
                contributionDao.findDetailsById(id),
                new Function<TContributionDetail, ContributionDetail>() {

                    @Override
                    public ContributionDetail apply(TContributionDetail t) {
                        return new ContributionDetail(
                                t.getBundle(),
                                t.getSection(),
                                t.getKey(),
                                t.getLocale(),
                                t.getOldValue(),
                                t.getNewValue()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ContributionSummary blankContribution(int branchId) {
        // Branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getProjectId());
        // Contribution mode
        boolean direct = securityUtils.isGranted(ProjectFunction.CONTRIBUTION_DIRECT, branch.getProjectId());
        // OK
        return new ContributionSummary(
                0,
                direct,
                branchId,
                "",
                accountService.getAccountSummary(securityUtils.getCurrentAccountId()),
                TimeUtils.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionSummary> getContributionList(int branchId) {
        return Lists.transform(
                contributionDao.findByBranch(branchId),
                contributionSummaryFunction
        );
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
        // Direct mode
        if (direct) {
            return save(branchId, input);
        }
        // Staging
        else {
            return stage(branchId, input);
        }
    }

    protected LocalizableMessage save(int branchId, ContributionInput input) {
        // Loads the branch configuration
        Configured<Object, TranslationSource<Object>> branchConfig = structureService.getConfiguredTranslationSource(branchId);
        // Gets the latest map version
        TranslationMap oldMap = branchConfig.getConfigurable().read(branchConfig.getConfiguration(), null);
        // Creates a diff from the input
        TranslationDiffBuilder diff = TranslationDiffBuilder.create();
        for (ContributionEntry entry : input.getContributions()) {
            diff.updated(
                    entry.getBundle(),
                    entry.getSection(),
                    entry.getKey()
            ).withDiff(entry.getLocale(), entry.getOldValue(), entry.getNewValue());
        }
        // Applies the diff
        TranslationMap newMap = oldMap.applyDiff(diff.build());
        // On the latest version
        branchConfig.getConfigurable().write(
                branchConfig.getConfiguration(),
                newMap,
                input.getMessage()
        );
        // Deletes the contribution
        int id = input.getId();
        if (id > 0) {
            contributionDao.delete(id);
        }
        // TODO #42 Sends a mail to the contributor
        // OK
        return new LocalizableMessage("contribution.saved");
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
        // Loads the contribution summary
        ContributionSummary contribution = getContribution(contributionId);
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
        model.add("contribution", resourceService.getContribution(locale, contribution));
        model.add("branch", resourceService.getBranch(locale, branch));
        model.add("project", resourceService.getProject(locale, project));
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
