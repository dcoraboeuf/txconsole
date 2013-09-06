package net.txconsole.web.controller;

import net.sf.jstring.Strings;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.service.StructureService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.ErrorHandler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIControllerTest {

    private UIController controller;
    private ErrorHandler errorHandler;
    private Strings strings;
    private StructureService structureService;

    @Before
    public void before() {
        errorHandler = mock(ErrorHandler.class);
        strings = mock(Strings.class);
        structureService = mock(StructureService.class);
        controller = new UIController(errorHandler, strings, structureService);
        // Current request
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    public void home() {
        Resource<String> r = controller.home();
        assertEquals("home", r.getData());
        assertEquals("http://localhost/ui", r.getLink("self").getHref());
        assertEquals("http://localhost/", r.getLink("gui").getHref());
        assertEquals("http://localhost/ui/project", r.getLink("projectList").getHref());
    }

    @Test
    public void projectList() {
        when(structureService.getProjects()).thenReturn(
                Arrays.asList(
                        new ProjectSummary(1, "P1", "Project 1"),
                        new ProjectSummary(2, "P2", "Project 2")
                )
        );
        List<Resource<ProjectSummary>> list = controller.projectList();
        assertEquals(2, list.size());
        {
            Resource<ProjectSummary> p = list.get(0);
            assertEquals("P1", p.getData().getName());
            assertEquals("http://localhost/ui/project/1", p.getLink("self").getHref());
            assertEquals("http://localhost/project/1", p.getLink("gui").getHref());
        }
        {
            Resource<ProjectSummary> p = list.get(1);
            assertEquals("P2", p.getData().getName());
            assertEquals("http://localhost/ui/project/2", p.getLink("self").getHref());
            assertEquals("http://localhost/project/2", p.getLink("gui").getHref());
        }
    }

    @Test
    public void projectGet() {
        when(structureService.getProject(1)).thenReturn(
                new ProjectSummary(1, "P1", "Project 1")
        );
        Resource<ProjectSummary> p = controller.projectGet(1);
        assertEquals("P1", p.getData().getName());
        assertEquals("http://localhost/ui/project/1", p.getLink("self").getHref());
        assertEquals("http://localhost/project/1", p.getLink("gui").getHref());
    }

    @Test
    public void projectCreate() {
        when(structureService.createProject(
                new ProjectCreationForm(
                        "P3",
                        "Project 3"
                )
        )).thenReturn(
                new ProjectSummary(
                        100,
                        "P3",
                        "Project 3"
                )
        );
        Resource<ProjectSummary> p = controller.projectCreate(
                new ProjectCreationForm(
                        "P3",
                        "Project 3"
                )
        );
        assertEquals(100, p.getData().getId());
        assertEquals("P3", p.getData().getName());
        assertEquals("Project 3", p.getData().getFullName());
        assertEquals("http://localhost/ui/project/100", p.getLink("self").getHref());
        assertEquals("http://localhost/project/100", p.getLink("gui").getHref());
    }

}
