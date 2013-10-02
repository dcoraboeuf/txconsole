package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TAccount;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ID;

import java.util.List;
import java.util.Locale;

public interface AccountDao {

    TAccount findByNameAndPassword(String name, String password);

    String getRoleByModeAndName(String mode, String name);

    TAccount findByModeAndName(String mode, String name);

    TAccount getByID(int id);

    List<TAccount> findAll();

    int createAccount(String name, String fullName, String email, String roleName, String mode, String password);

    void deleteAccount(int id);

    void updateAccount(int id, String name, String fullName, String email, String roleName);

    Ack changePassword(int id, String oldPassword, String newPassword);

    Ack changeEmail(int id, String password, String email);

    Ack resetPassword(int id, String password);

    Ack changeLanguage(int accountId, Locale lang);

    List<TAccount> getUserAccounts();

    List<TAccount> findByQuery(String query);
}
