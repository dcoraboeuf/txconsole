package net.txconsole.core.security;

import net.txconsole.core.model.Account;
import net.txconsole.core.model.Signature;

import java.util.concurrent.Callable;

public interface SecurityUtils {

    boolean isLogged();

    Account getCurrentAccount();

    int getCurrentAccountId();

    boolean isAdmin();

    boolean hasRole(String role);

    void checkIsAdmin();

    void checkIsLogged();

    <T> T asAdmin(Callable<T> call);

    boolean isGranted(SecurityFunction fn, int id);

    void checkGrant(SecurityFunction fn, int id);

    Signature getCurrentSignature();
}
