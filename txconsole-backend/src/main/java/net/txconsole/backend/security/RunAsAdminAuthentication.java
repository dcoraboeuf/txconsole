package net.txconsole.backend.security;

import net.txconsole.core.model.Account;
import net.txconsole.core.security.SecurityRoles;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Locale;

public class RunAsAdminAuthentication extends AbstractAuthenticationToken {

    public RunAsAdminAuthentication() {
        super(AuthorityUtils.createAuthorityList(SecurityRoles.ADMINISTRATOR));
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getDetails() {
        return new Account(
                1, // Same as admin
                "batch",
                "Batch",
                "",
                SecurityRoles.ADMINISTRATOR,
                "builtin",
                Locale.ENGLISH
        );
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
