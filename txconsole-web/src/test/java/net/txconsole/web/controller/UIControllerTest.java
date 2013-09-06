package net.txconsole.web.controller;

import net.txconsole.core.model.PipelineCreationForm;
import net.txconsole.core.model.PipelineSummary;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.StructureService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.ErrorHandler;
import net.sf.jstring.Strings;
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
    private SecurityUtils securityUtils;

    @Before
    public void before() {
        errorHandler = mock(ErrorHandler.class);
        strings = mock(Strings.class);
        structureService = mock(StructureService.class);
        securityUtils = mock(SecurityUtils.class);
        controller = new UIController(errorHandler, strings, structureService, securityUtils);
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
        assertEquals("http://localhost/ui/pipeline", r.getLink("pipelineList").getHref());
    }

    @Test
    public void pipelineList() {
        when(structureService.getPipelines()).thenReturn(
                Arrays.asList(
                        new PipelineSummary(1, "P1", "Pipeline 1"),
                        new PipelineSummary(2, "P2", "Pipeline 2")
                )
        );
        List<Resource<PipelineSummary>> list = controller.pipelineList();
        assertEquals(2, list.size());
        {
            Resource<PipelineSummary> p = list.get(0);
            assertEquals("P1", p.getData().getName());
            assertEquals("http://localhost/ui/pipeline/1", p.getLink("self").getHref());
            assertEquals("http://localhost/pipeline/P1", p.getLink("gui").getHref());
        }
        {
            Resource<PipelineSummary> p = list.get(1);
            assertEquals("P2", p.getData().getName());
            assertEquals("http://localhost/ui/pipeline/2", p.getLink("self").getHref());
            assertEquals("http://localhost/pipeline/P2", p.getLink("gui").getHref());
        }
    }

    @Test
    public void pipelineGet() {
        when(structureService.getPipeline(1)).thenReturn(
                new PipelineSummary(1, "P1", "Pipeline 1")
        );
        Resource<PipelineSummary> p = controller.pipelineGet(1);
        assertEquals("P1", p.getData().getName());
        assertEquals("http://localhost/ui/pipeline/1", p.getLink("self").getHref());
        assertEquals("http://localhost/pipeline/P1", p.getLink("gui").getHref());
    }

    @Test
    public void pipelineCreate() {
        when(structureService.createPipeline(
                new PipelineCreationForm(
                        "P3",
                        "Pipeline 3"
                )
        )).thenReturn(
                new PipelineSummary(
                        100,
                        "P3",
                        "Pipeline 3"
                )
        );
        Resource<PipelineSummary> p = controller.pipelineCreate(
                new PipelineCreationForm(
                        "P3",
                        "Pipeline 3"
                )
        );
        assertEquals(100, p.getData().getId());
        assertEquals("P3", p.getData().getName());
        assertEquals("Pipeline 3", p.getData().getDescription());
        assertEquals("http://localhost/ui/pipeline/100", p.getLink("self").getHref());
        assertEquals("http://localhost/pipeline/P3", p.getLink("gui").getHref());
    }

}
