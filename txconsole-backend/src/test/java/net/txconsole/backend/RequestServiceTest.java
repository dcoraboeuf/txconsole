package net.txconsole.backend;

import net.sf.jstring.Strings;
import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.backend.dao.model.TRequestEntry;
import net.txconsole.backend.exceptions.DeletedRequestEntryCannotBeEditedException;
import net.txconsole.backend.exceptions.RequestCannotBeEditedException;
import net.txconsole.core.config.CoreConfig;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.RequestEntryInput;
import net.txconsole.core.model.RequestStatus;
import net.txconsole.core.model.TranslationDiffType;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.EscapingService;
import net.txconsole.service.EventService;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import net.txconsole.service.support.TranslationSourceService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Locale;

import static org.mockito.Mockito.*;

public class RequestServiceTest {

    private StructureService structureService;
    private TranslationMapService translationMapService;
    private TranslationSourceService translationSourceService;
    private EventService eventService;
    private RequestDao requestDao;
    private SecurityUtils securityUtils;
    private EscapingService escapingService;
    private RequestServiceImpl service;

    @Before
    public void init() {
        structureService = mock(StructureService.class);
        translationMapService = mock(TranslationMapService.class);
        translationSourceService = mock(TranslationSourceService.class);
        eventService = mock(EventService.class);
        requestDao = mock(RequestDao.class);
        securityUtils = mock(SecurityUtils.class);
        escapingService = mock(EscapingService.class);
        Strings strings = new CoreConfig().strings();
        service = new RequestServiceImpl(
                structureService,
                translationMapService,
                translationSourceService,
                eventService,
                requestDao,
                securityUtils,
                escapingService,
                strings
        );
    }

    @Test(expected = AccessDeniedException.class)
    public void editRequestEntry_denied() {
        // Mocking
        when(requestDao.getRequestEntry(1000)).thenReturn(new TRequestEntry(
                1000,
                100,
                "common",
                "default",
                "one",
                TranslationDiffType.ADDED
        ));
        when(requestDao.getById(100)).thenReturn(new TRequest(
                100,
                10,
                "100",
                "110",
                null,
                RequestStatus.EXPORTED,
                null
        ));
        when(structureService.getBranch(10)).thenReturn(new BranchSummary(
                10,
                1,
                "BCH1"
        ));
        doThrow(new AccessDeniedException("Test")).when(securityUtils).checkGrant(ProjectFunction.REQUEST_EDIT, 1);
        // Call
        service.editRequestEntry(Locale.ENGLISH, 1000, new RequestEntryInput(Locale.FRENCH, "Valeur"));
    }

    @Test(expected = RequestCannotBeEditedException.class)
    public void editRequestEntry_wrong_status() {
        // Mocking
        when(requestDao.getRequestEntry(1000)).thenReturn(new TRequestEntry(
                1000,
                100,
                "common",
                "default",
                "one",
                TranslationDiffType.ADDED
        ));
        when(requestDao.getById(100)).thenReturn(new TRequest(
                100,
                10,
                "100",
                "110",
                null,
                RequestStatus.CLOSED,
                null
        ));
        when(structureService.getBranch(10)).thenReturn(new BranchSummary(
                10,
                1,
                "BCH1"
        ));
        // Call
        service.editRequestEntry(Locale.ENGLISH, 1000, new RequestEntryInput(Locale.FRENCH, "Valeur"));
    }

    @Test(expected = DeletedRequestEntryCannotBeEditedException.class)
    public void editRequestEntry_deleted() {
        // Mocking
        when(requestDao.getRequestEntry(1000)).thenReturn(new TRequestEntry(
                1000,
                100,
                "common",
                "default",
                "one",
                TranslationDiffType.DELETED
        ));
        when(requestDao.getById(100)).thenReturn(new TRequest(
                100,
                10,
                "100",
                "110",
                null,
                RequestStatus.EXPORTED,
                null
        ));
        when(structureService.getBranch(10)).thenReturn(new BranchSummary(
                10,
                1,
                "BCH1"
        ));
        // Call
        service.editRequestEntry(Locale.ENGLISH, 1000, new RequestEntryInput(Locale.FRENCH, "Valeur"));
    }

}
