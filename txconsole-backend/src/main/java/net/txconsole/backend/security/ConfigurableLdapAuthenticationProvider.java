package net.txconsole.backend.security;

import net.txconsole.core.model.Account;
import net.txconsole.core.model.AccountCreationForm;
import net.txconsole.core.security.SecurityRoles;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.AccountService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.concurrent.Callable;

@Service
public class ConfigurableLdapAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;
    private final LDAPProviderFactory ldapProviderFactory;
    private final SecurityUtils securityUtils;

    @Autowired
    public ConfigurableLdapAuthenticationProvider(AccountService accountService, LDAPProviderFactory ldapProviderFactory, SecurityUtils securityUtils) {
        this.accountService = accountService;
        this.ldapProviderFactory = ldapProviderFactory;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Gets the (cached) provider
        LdapAuthenticationProvider ldapAuthenticationProvider = ldapProviderFactory.getProvider();
        // If not enabled, cannot authenticate!
        if (ldapAuthenticationProvider == null) {
            return null;
        }
        // LDAP connection
        else {
            Authentication ldapAuthentication = ldapAuthenticationProvider.authenticate(authentication);
            if (ldapAuthentication != null && ldapAuthentication.isAuthenticated()) {
                // Gets the account name
                final String name = ldapAuthentication.getName();
                // Gets any existing account
                Account account = accountService.getAccount("ldap", name);
                if (account == null) {
                    // If not found, auto-registers the account using the LDAP details
                    Object principal = ldapAuthentication.getPrincipal();
                    if (principal instanceof PersonLDAPUserDetails) {
                        final PersonLDAPUserDetails details = (PersonLDAPUserDetails) principal;
                        // Auto-registration if email is OK
                        if (StringUtils.isNotBlank(details.getEmail())) {
                            // Registration
                            account = securityUtils.asAdmin(new Callable<Account>() {
                                @Override
                                public Account call() throws Exception {
                                    return accountService.createAccount(new AccountCreationForm(
                                            name,
                                            details.getFullName(),
                                            details.getEmail(),
                                            SecurityRoles.USER,
                                            "ldap",
                                            "",
                                            ""
                                    ));
                                }
                            });
                        } else {
                            // Temporary account
                            account = new Account(0, name, details.getFullName(), "", SecurityRoles.USER, "ldap", Locale.ENGLISH);
                        }
                    } else {
                        // Temporary account
                        account = new Account(0, name, name, "", SecurityRoles.USER, "ldap", Locale.ENGLISH);
                    }
                }
                // OK
                return new AccountAuthentication(account);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
