package net.txconsole.backend.security;

import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.service.StructureService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StructureServiceIntegrationSecurityTest extends AbstractSecurityTest {

    @Autowired
    private StructureService structureService;

    @Test
    public void project_list_anonymous_ok() throws Exception {
        asAnonymous().call(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                structureService.getProjects();
                return null;
            }
        });
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void project_create_anonymous_denied() throws Exception {
        asAnonymous().call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                structureService.createProject(new ProjectCreationForm(
                        "project_anonymous",
                        "Cannot create a project",
                        Arrays.asList("en"),
                        null));
                return null;
            }
        });
    }

    @Test(expected = AccessDeniedException.class)
    public void project_create_user_denied() throws Exception {
        asUser().call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                structureService.createProject(new ProjectCreationForm(
                        "project_user",
                        "Cannot create a project",
                        Arrays.asList("en"),
                        null));
                return null;
            }
        });
    }

    @Test
    public void project_create_admin_ok() throws Exception {
        ProjectSummary project = createProject("project_admin");
        assertNotNull(project);
        assertEquals("project_admin", project.getName());
    }

    private ProjectSummary createProject(final String name) throws Exception {
        return asAdmin().call(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() throws Exception {
                return structureService.createProject(new ProjectCreationForm(
                        name,
                        "OK",
                        Arrays.asList("en"),
                        new JsonConfiguration(
                                "mock",
                                null
                        )));
            }
        });
    }

}
