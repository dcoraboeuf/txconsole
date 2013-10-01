package net.txconsole.backend;

import net.txconsole.backend.security.AccountAuthentication;
import net.txconsole.backend.support.MockTranslationSourceConfig;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityRoles;
import net.txconsole.extension.exchange.properties.PropertiesTxFileExchange;
import net.txconsole.extension.exchange.properties.PropertiesTxFileExchangeConfig;
import net.txconsole.service.RequestService;
import net.txconsole.service.StructureService;
import net.txconsole.test.AbstractIntegrationTest;
import net.txconsole.test.Helper;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Callable;

@ContextConfiguration({"classpath:META-INF/backend-security-aop.xml"})
public abstract class AbstractBackendTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StructureService structureService;
    @Autowired
    private RequestService requestService;

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

    protected String uid(String prefix) {
        return Helper.uid(prefix);
    }

    protected RequestSummary doCreateRequest() throws Exception {
        final BranchSummary b = doCreateBranch();
        return asAdmin().call(new Callable<RequestSummary>() {
            @Override
            public RequestSummary call() throws Exception {
                return requestService.createRequest(
                        b.getId(),
                        new RequestCreationForm(
                                "1.0",
                                new JsonConfiguration(
                                        PropertiesTxFileExchange.ID,
                                        objectMapper.valueToTree(
                                                new PropertiesTxFileExchangeConfig()
                                        )
                                )
                        )
                );
            }
        });
    }

    protected BranchSummary doCreateBranch() throws Exception {
        final ProjectSummary p = doCreateProject();
        return asAdmin().call(new Callable<BranchSummary>() {
            @Override
            public BranchSummary call() throws Exception {
                return structureService.createBranch(p.getId(), new BranchCreationForm(
                        uid("BCH"),
                        Collections.<ParameterValueForm>emptyList()
                ));
            }
        });
    }

    protected ProjectSummary doCreateProject() throws Exception {
        return doCreateProject(uid("PRJ"));
    }

    private ProjectSummary doCreateProject(final String name) throws Exception {
        return asAdmin().call(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() throws Exception {
                return structureService.createProject(new ProjectCreationForm(
                        name,
                        "Project " + name,
                        new JsonConfiguration(
                                "mock",
                                objectMapper.valueToTree(
                                        new MockTranslationSourceConfig(name)
                                )
                        )
                )
                );
            }
        });
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

        public UserCall withProjectGrant(int id, ProjectFunction fn) {
            account.withACL(fn, id);
            return this;
        }
    }

    protected static class AdminCall extends AccountCall {

        public AdminCall() {
            super(new Account(1, "admin", "Administrator",
                    "admin@test.com", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH));
        }
    }

}
