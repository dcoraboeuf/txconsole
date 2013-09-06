package net.txconsole.backend.security;

import net.txconsole.core.model.Account;
import net.txconsole.core.security.SecurityRoles;
import net.txconsole.service.security.PipelineFunction;
import net.txconsole.test.AbstractIntegrationTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;

import java.util.Locale;
import java.util.concurrent.Callable;

@ContextConfiguration({"classpath:META-INF/backend-security-aop.xml"})
public abstract class AbstractSecurityTest extends AbstractIntegrationTest {

    protected AnonymousCall asAnonymous() {
        return new AnonymousCall();
    }

    protected UserCall asUser() {
        return new UserCall();
    }

    protected AdminCall asAdmin() {
        return new AdminCall();
    }

    protected <T> T withContext(Callable<T> call, Runnable contextSetup) throws Exception {
        // Gets the current context
        SecurityContext oldContext = SecurityContextHolder.getContext();
        try {
            // Sets the new context
            contextSetup.run();
            // Call
            return call.call();
        } catch (Exception e) {
            throw e;
        } finally {
            // Restores the context
            SecurityContextHolder.setContext(oldContext);
        }
    }

    protected static interface ContextCall {

        <T> T call(Callable<T> call) throws Exception;
    }

    protected static abstract class AbstractContextCall implements ContextCall {

        @Override
        public <T> T call(Callable<T> call) throws Exception {
            // Gets the current context
            SecurityContext oldContext = SecurityContextHolder.getContext();
            try {
                // Sets the new context
                contextSetup();
                // Call
                return call.call();
            } catch (Exception e) {
                throw e;
            } finally {
                // Restores the context
                SecurityContextHolder.setContext(oldContext);
            }
        }

        protected abstract void contextSetup();
    }

    protected static class AnonymousCall extends AbstractContextCall {

        @Override
        protected void contextSetup() {
            SecurityContextHolder.clearContext();
        }
    }

    protected static class AccountCall extends AbstractContextCall {

        protected final Account account;

        public AccountCall(Account account) {
            this.account = account;
        }

        @Override
        protected void contextSetup() {
            SecurityContext context = new SecurityContextImpl();
            Authentication authentication = new AccountAuthentication(account);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }

    protected static class UserCall extends AccountCall {

        public UserCall() {
            super(new Account(1, "user", "Normal user",
                    "user@test.com", SecurityRoles.USER, "builtin", Locale.ENGLISH));
        }

        public UserCall withPipelineGrant(int id, PipelineFunction fn) {
            account.withACL("PIPELINE", id, fn.name());
            return this;
        }
    }

    protected static class AdminCall extends AccountCall {

        public AdminCall() {
            super(new Account(0, "admin", "Administrator",
                    "admin@test.com", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH));
        }
    }

}
