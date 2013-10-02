package net.txconsole.backend;

import net.txconsole.backend.support.MockTranslationSource;
import net.txconsole.backend.support.MockTranslationSourceConfig;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.RequestCreationForm;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.core.security.ProjectFunction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;

public class RequestServiceIntegrationTest extends AbstractBackendTest {

    @Test(expected = AccessDeniedException.class)
    public void createRequest_anonymous_rejected() throws Exception {
        // Creates any branch
        final BranchSummary branch = doCreateBranch();
        // Creates a request
        asAnonymous().call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                requestService.createRequest(branch.getId(), new RequestCreationForm(
                        "1",
                        new JsonConfiguration(
                                MockTranslationSource.ID,
                                objectMapper.valueToTree(new MockTranslationSourceConfig("test"))
                        )
                ));
                return null;
            }
        });
    }

    @Test(expected = AccessDeniedException.class)
    public void createRequest_any_user_rejected() throws Exception {
        // Creates any branch
        final BranchSummary branch = doCreateBranch();
        // Creates a request
        asUser().call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                requestService.createRequest(branch.getId(), new RequestCreationForm(
                        "1",
                        new JsonConfiguration(
                                MockTranslationSource.ID,
                                objectMapper.valueToTree(new MockTranslationSourceConfig("test"))
                        )
                ));
                return null;
            }
        });
    }

    @Test
    public void createRequest_owner_granted() throws Exception {
        // Creates any branch
        final BranchSummary branch = doCreateBranch();
        // Creates a request
        RequestSummary request = asUser().withProjectGrant(branch.getProjectId(), ProjectFunction.REQUEST_CREATE).call(new Callable<RequestSummary>() {
            @Override
            public RequestSummary call() throws Exception {
                return requestService.createRequest(branch.getId(), new RequestCreationForm(
                        "1",
                        new JsonConfiguration(
                                MockTranslationSource.ID,
                                objectMapper.valueToTree(new MockTranslationSourceConfig("test"))
                        )
                ));
            }
        });
        // Created
        assertNotNull(request);
    }

    @Test
    public void createRequest_admin_granted() throws Exception {
        // Creates any branch
        final BranchSummary branch = doCreateBranch();
        // Creates a request
        RequestSummary request = asAdmin().call(new Callable<RequestSummary>() {
            @Override
            public RequestSummary call() throws Exception {
                return requestService.createRequest(branch.getId(), new RequestCreationForm(
                        "1",
                        new JsonConfiguration(
                                MockTranslationSource.ID,
                                objectMapper.valueToTree(new MockTranslationSourceConfig("test"))
                        )
                ));
            }
        });
        // Created
        assertNotNull(request);
    }

}
