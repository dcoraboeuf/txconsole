package net.txconsole.backend.security;

import net.txconsole.core.model.Account;
import net.txconsole.core.security.SecurityRoles;
import net.txconsole.core.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class SecurityUtilsImpl implements SecurityUtils {

    @Override
    public boolean isLogged() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && (authentication.getDetails() instanceof Account));
    }

    @Override
    public Account getCurrentAccount() {
        if (isLogged()) {
            return (Account) SecurityContextHolder.getContext().getAuthentication().getDetails();
        } else {
            return null;
        }
    }

    @Override
    public int getCurrentAccountId() {
        Account account = getCurrentAccount();
        return account != null ? account.getId() : -1;
    }

    @Override
    public boolean isAdmin() {
        Account account = getCurrentAccount();
        return account != null && SecurityRoles.ADMINISTRATOR.equals(account.getRoleName());
    }

    @Override
    public void checkIsAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Administrator right is required");
        }
    }

    @Override
    public void checkIsLogged() {
        if (!isLogged()) {
            throw new AccessDeniedException("Authentication is required");
        }
    }

    @Override
    public boolean hasRole(String role) {
        Account account = getCurrentAccount();
        if (account != null) {
            switch (account.getRoleName()) {
                case SecurityRoles.ADMINISTRATOR:
                    return true;
                case SecurityRoles.USER:
                    return SecurityRoles.USER.equals(role);
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public <T> T asAdmin(Callable<T> call) {
        // Current context
        final SecurityContext context = SecurityContextHolder.getContext();
        try {
            // Creates a temporary admin context
            SecurityContextImpl adminContext = new SecurityContextImpl();
            adminContext.setAuthentication(new RunAsAdminAuthentication());
            SecurityContextHolder.setContext(adminContext);
            // Runs the call
            try {
                return call.call();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception e) {
                throw new AsAdminCallException(e);
            }
        } finally {
            // Restores the initial context
            SecurityContextHolder.setContext(context);
        }
    }

    @Override
    public boolean isGranted(String category, int id, String action) {
        Account account = getCurrentAccount();
        return account != null && account.isGranted(category, id, action);
    }

    @Override
    public void checkGrant(String category, int id, String action) {
        if (!isGranted(category, id, action)) {
            throw new AccessDeniedException("Access is not granted");
        }
    }
}
