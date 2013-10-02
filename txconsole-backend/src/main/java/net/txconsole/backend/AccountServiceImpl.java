package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.backend.dao.AccountDao;
import net.txconsole.backend.dao.ProjectAuthorizationDao;
import net.txconsole.backend.dao.model.TAccount;
import net.txconsole.backend.dao.model.TProjectAuthorization;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityRoles;
import net.txconsole.core.validation.AccountValidation;
import net.txconsole.core.validation.Validations;
import net.txconsole.service.AccountService;
import net.txconsole.service.security.AdminGrant;
import net.txconsole.service.security.ProjectGrant;
import net.txconsole.service.security.ProjectGrantId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class AccountServiceImpl extends AbstractValidatorService implements AccountService {

    private final Strings strings;
    private final AccountDao accountDao;
    private final Function<TAccount, Account> accountFunction = new Function<TAccount, Account>() {
        @Override
        public Account apply(TAccount t) {
            if (t == null) {
                return null;
            } else {
                return new Account(
                        t.getId(),
                        t.getName(),
                        t.getFullName(),
                        t.getEmail(),
                        t.getRoleName(),
                        t.getMode(),
                        strings.getSupportedLocales().filterForLookup(t.getLocale())
                );
            }
        }
    };
    private final Function<TAccount, Account> accountACLFunction = new Function<TAccount, Account>() {
        @Override
        public Account apply(TAccount t) {
            return getACL(accountFunction.apply(t));
        }
    };
    private final Function<TAccount, AccountSummary> accountSummaryFn = new Function<TAccount, AccountSummary>() {
        @Override
        public AccountSummary apply(TAccount t) {
            return new AccountSummary(t.getId(), t.getName(), t.getFullName());
        }
    };
    private final ProjectAuthorizationDao projectAuthorizationDao;

    @Autowired
    public AccountServiceImpl(ValidatorService validatorService, Strings strings, AccountDao accountDao, ProjectAuthorizationDao projectAuthorizationDao) {
        super(validatorService);
        this.strings = strings;
        this.accountDao = accountDao;
        this.projectAuthorizationDao = projectAuthorizationDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Account authenticate(String user, String password) {
        return accountACLFunction.apply(accountDao.findByNameAndPassword(user, password));
    }

    @Override
    @Transactional(readOnly = true)
    public String getRole(String mode, String user) {
        return accountDao.getRoleByModeAndName(mode, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(String mode, String user) {
        return accountACLFunction.apply(
                accountDao.findByModeAndName(mode, user)
        );
    }

    @Override
    @Transactional(readOnly = true)
    @AdminGrant
    public Account getAccount(int id) {
        return accountFunction.apply(
                accountDao.getByID(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AccountSummary getAccountSummary(int id) {
        return accountSummaryFn.apply(
                accountDao.getByID(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountSummary> accountLookup(String query) {
        return Lists.transform(
                accountDao.findByQuery(query),
                accountSummaryFn
        );
    }

    @Override
    @Transactional
    @ProjectGrant(ProjectFunction.ACL)
    public Ack setProjectACL(@ProjectGrantId int project, int account, ProjectRole role) {
        return projectAuthorizationDao.set(project, account, role);
    }

    @Override
    @Transactional
    @ProjectGrant(ProjectFunction.ACL)
    public Ack unsetProjectACL(@ProjectGrantId int project, int account) {
        return projectAuthorizationDao.unset(project, account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectAuthorization> getProjectACLList(int project) {
        return Lists.transform(
                projectAuthorizationDao.findByProject(project),
                new Function<TProjectAuthorization, ProjectAuthorization>() {
                    @Override
                    public ProjectAuthorization apply(TProjectAuthorization t) {
                        return new ProjectAuthorization(
                                t.getProject(),
                                accountSummaryFn.apply(accountDao.getByID(t.getAccount())),
                                t.getRole()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    @AdminGrant
    public List<Account> getAccounts() {
        return Lists.transform(
                accountDao.findAll(),
                accountFunction
        );
    }

    @Override
    @Transactional
    @AdminGrant
    public Account createAccount(final AccountCreationForm form) {
        // Validation
        validate(form, AccountValidation.class);
        // Validation: role
        validate(form.getRoleName(),
                Validations.oneOf(SecurityRoles.ALL),
                "net.txconsole.core.model.Account.roleName.incorrect",
                StringUtils.join(SecurityRoles.ALL, ","));
        // Validation: mode
        // TODO Gets the list of modes from the registered services
        List<String> modes = Arrays.asList("builtin", "ldap");
        validate(form.getMode(),
                Validations.oneOf(modes),
                "net.txconsole.core.model.Account.mode.incorrect",
                StringUtils.join(modes, ","));
        // Validation: checks the password
        validate(form.getPassword(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !"builtin".equals(form.getMode()) || StringUtils.isNotBlank(input);
            }
        }, "net.txconsole.core.model.Account.password.requiredForBuiltin");
        // OK
        return getAccount(
                accountDao.createAccount(
                        form.getName(),
                        form.getFullName(),
                        form.getEmail(),
                        form.getRoleName(),
                        form.getMode(),
                        form.getPassword()
                )
        );
    }

    @Override
    @Transactional
    @AdminGrant
    public void deleteAccount(int id) {
        accountDao.deleteAccount(id);
    }

    @Override
    @Transactional
    @AdminGrant
    public Account updateAccount(int id, AccountUpdateForm form) {
        // Updates the account
        accountDao.updateAccount(
                id,
                form.getName(),
                form.getFullName(),
                form.getEmail(),
                form.getRoleName()
        );
        // OK
        return getAccount(id);
    }

    @Override
    @Transactional
    @AdminGrant
    public Ack changePassword(int id, PasswordChangeForm form) {
        // Gets the existing account
        Account account = getAccount(id);
        // Checks the mode
        if ("builtin".equals(account.getMode())) {
            // DAO
            return accountDao.changePassword(id, form.getOldPassword(), form.getNewPassword());
        } else {
            // Cannot change password in this case
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    @AdminGrant
    public Ack changeEmail(int id, EmailChangeForm form) {
        // Gets the existing account
        Account account = getAccount(id);
        // Checks the mode
        if ("builtin".equals(account.getMode())) {
            // DAO
            return accountDao.changeEmail(id, form.getPassword(), form.getEmail());
        } else {
            // Cannot change password in this case
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    @AdminGrant
    public Account resetPassword(int id, String password) {
        // Gets the existing account
        Account account = getAccount(id);
        // Checks the mode
        if ("builtin".equals(account.getMode())) {
            // DAO
            accountDao.resetPassword(id, password);
        }
        // OK
        return getAccount(id);
    }

    @Override
    @Transactional
    @AdminGrant
    public Ack changeLanguage(int id, String lang) {
        return accountDao.changeLanguage(id, strings.getSupportedLocales().filterForLookup(new Locale(lang)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountSummary> getUserAccounts() {
        return Lists.transform(
                accountDao.getUserAccounts(),
                accountSummaryFn
        );
    }

    protected Account getACL(Account account) {
        if (account != null) {
            // Functions for all projects
            List<TProjectAuthorization> authList = projectAuthorizationDao.findByAccount(account.getId());
            for (TProjectAuthorization auth : authList) {
                switch (auth.getRole()) {
                    case OWNER:
                        account = account.withACL(ProjectFunction.UPDATE, auth.getProject());
                        account = account.withACL(ProjectFunction.REQUEST_CREATE, auth.getProject());
                        account = account.withACL(ProjectFunction.REQUEST_MERGE, auth.getProject());
                        account = account.withACL(ProjectFunction.REQUEST_DELETE, auth.getProject());
                        account = account.withACL(ProjectFunction.ACL, auth.getProject());
                        // ... applies everything below
                    case TRANSLATOR:
                        account = account.withACL(ProjectFunction.REQUEST_UPLOAD, auth.getProject());
                        // ... applies everything below
                    case REVIEWER:
                        account = account.withACL(ProjectFunction.REQUEST_EDIT, auth.getProject());
                        // ... applies everything below
                    case CONTRIBUTOR:
                        // ... applies everything below
                    default:
                }
            }
        }
        // OK
        return account;
    }
}
