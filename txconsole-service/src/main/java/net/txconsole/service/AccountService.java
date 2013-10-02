package net.txconsole.service;

import net.txconsole.core.model.*;

import java.util.List;

public interface AccountService {

    Account authenticate(String user, String password);

    String getRole(String mode, String user);

    Account getAccount(String mode, String user);

    Account getAccount(int id);

    List<Account> getAccounts();

    Account createAccount(AccountCreationForm form);

    void deleteAccount(int id);

    Account updateAccount(int id, AccountUpdateForm form);

    Ack changePassword(int id, PasswordChangeForm form);

    Ack changeEmail(int accountId, EmailChangeForm form);

    Account resetPassword(int id, String password);

    Ack changeLanguage(int id, String lang);

    List<AccountSummary> getUserAccounts();

    AccountSummary getAccountSummary(int account);

    List<AccountSummary> accountLookup(String query);

    Ack setProjectACL(int project, int account, ProjectRole role);
}
