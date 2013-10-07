package net.txconsole.web.controller;

import net.sf.jstring.Strings;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.core.model.Resource;
import net.txconsole.web.support.ErrorHandler;
import net.txconsole.web.support.GUIEventService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIControllerTest {

    private UIController controller;
    private ErrorHandler errorHandler;
    private Strings strings;
    private StructureService structureService;
    private SecurityUtils securityUtils;
    private TranslationMapService translationMapService;
    private GUIEventService guiEventService;

    @Before
    public void before() {
        errorHandler = mock(ErrorHandler.class);
        strings = mock(Strings.class);
        structureService = mock(StructureService.class);
        securityUtils = mock(SecurityUtils.class);
        translationMapService = mock(TranslationMapService.class);
        guiEventService = mock(GUIEventService.class);
        controller = new UIController(errorHandler, strings, structureService, translationMapService, guiEventService, securityUtils);
        // Current request
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    public void home() {
        Resource<String> r = controller.home(Locale.ENGLISH);
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
        List<Resource<ProjectSummary>> list = controller.getProjectList(Locale.ENGLISH);
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
        Resource<ProjectSummary> p = controller.getProject(Locale.ENGLISH, 1);
        assertEquals("P1", p.getData().getName());
        assertEquals("http://localhost/ui/project/1", p.getLink("self").getHref());
        assertEquals("http://localhost/project/1", p.getLink("gui").getHref());
    }

    @Test
    public void projectCreate() {
        when(structureService.createProject(
                new ProjectCreationForm(
                        "P3",
                        "Project 3",
                        null
                )
        )).thenReturn(
                new ProjectSummary(
                        100,
                        "P3",
                        "Project 3"
                )
        );
        Resource<ProjectSummary> p = controller.createProject(
                Locale.ENGLISH,
                new ProjectCreationForm(
                        "P3",
                        "Project 3",
                        null
                )
        );
        assertEquals(100, p.getData().getId());
        assertEquals("P3", p.getData().getName());
        assertEquals("Project 3", p.getData().getFullName());
        assertEquals("http://localhost/ui/project/100", p.getLink("self").getHref());
        assertEquals("http://localhost/project/100", p.getLink("gui").getHref());
    }

    @Test
    public void branchACL() {
        // Branch summary
        when(structureService.getBranch(10)).thenReturn(
                new BranchSummary(
                        10,
                        1,
                        "B1"
                )
        );
        // Grants
        when(securityUtils.isGranted(ProjectFunction.REQUEST_CREATE, 1)).thenReturn(true);
        // Call
        Resource<BranchSummary> resource = controller.getBranch(Locale.ENGLISH, 10);
        // Basic checks
        assertNotNull(resource);
        assertEquals(10, resource.getData().getId());
        assertEquals(1, resource.getData().getProjectId());
        assertEquals("B1", resource.getData().getName());
        // Grants
        assertTrue(resource.getActions().contains("PROJECT#REQUEST_CREATE"));
        assertFalse(resource.getActions().contains("PROJECT#UPDATE"));
        assertFalse(resource.getActions().contains("PROJECT#DELETE"));
    }

    @Test
    public void branchACL_admin() {
        // Branch summary
        when(structureService.getBranch(10)).thenReturn(
                new BranchSummary(
                        10,
                        1,
                        "B1"
                )
        );
        // Grants
        when(securityUtils.isGranted(ProjectFunction.REQUEST_CREATE, 1)).thenReturn(true);
        when(securityUtils.isGranted(ProjectFunction.UPDATE, 1)).thenReturn(true);
        when(securityUtils.isGranted(ProjectFunction.DELETE, 1)).thenReturn(true);
        // Call
        Resource<BranchSummary> resource = controller.getBranch(Locale.ENGLISH, 10);
        // Basic checks
        assertNotNull(resource);
        assertEquals(10, resource.getData().getId());
        assertEquals(1, resource.getData().getProjectId());
        assertEquals("B1", resource.getData().getName());
        // Grants
        assertTrue(resource.getActions().contains("PROJECT#REQUEST_CREATE"));
        assertTrue(resource.getActions().contains("PROJECT#UPDATE"));
        assertTrue(resource.getActions().contains("PROJECT#DELETE"));
    }

}
