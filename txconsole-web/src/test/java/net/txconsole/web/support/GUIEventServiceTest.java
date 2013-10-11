package net.txconsole.web.support;

import net.sf.jstring.Strings;
import net.txconsole.core.config.CoreConfig;
import net.txconsole.core.model.Event;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.Signature;
import net.txconsole.core.support.MapBuilder;
import net.txconsole.service.EventService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GUIEventServiceTest {

    @Test
    public void message_branch_created() {
        EventService eventService = mock(EventService.class);
        Strings strings = new CoreConfig().strings();
        GUIEventService service = new GUIEventServiceImpl(eventService, strings);
        Event event = new Event(
                10,
                EventCode.BRANCH_CREATED,
                Arrays.asList(
                        "MyBranch",
                        "MyProject"
                ),
                new Signature(
                        11,
                        "Damien",
                        new DateTime(2013, 8, 7, 12, 15, DateTimeZone.UTC)
                ),
                MapBuilder.dual(EventEntity.BRANCH, 2, EventEntity.PROJECT, 1)
        );
        String message = service.getEventMessage(
                Locale.ENGLISH,
                event
        );
        assertEquals("Branch <a class=\"event-entity\" href=\"branch/2\">MyBranch</a> has been created for project <a class=\"event-entity\" href=\"project/1\">MyProject</a>.", message);
    }

    @Test
    public void message_branch_deleted() {
        EventService eventService = mock(EventService.class);
        Strings strings = new CoreConfig().strings();
        GUIEventService service = new GUIEventServiceImpl(eventService, strings);
        Event event = new Event(
                10,
                EventCode.BRANCH_DELETED,
                Arrays.asList(
                        "MyBranch",
                        "MyProject"
                ),
                new Signature(
                        11,
                        "Damien",
                        new DateTime(2013, 8, 7, 12, 15, DateTimeZone.UTC)
                ),
                MapBuilder.singleton(EventEntity.PROJECT, 1)
        );
        String message = service.getEventMessage(
                Locale.ENGLISH,
                event
        );
        assertEquals("Branch <span class=\"event-entity\">MyBranch</span> has been deleted for project <a class=\"event-entity\" href=\"project/1\">MyProject</a>.", message);
    }

    @Test
    public void message_branch_created_and_delettion() {
        EventService eventService = mock(EventService.class);
        Strings strings = new CoreConfig().strings();
        GUIEventService service = new GUIEventServiceImpl(eventService, strings);
        Event event = new Event(
                10,
                EventCode.BRANCH_CREATED,
                Arrays.asList(
                        "MyBranch",
                        "MyProject"
                ),
                new Signature(
                        11,
                        "Damien",
                        new DateTime(2013, 8, 7, 12, 15, DateTimeZone.UTC)
                ),
                MapBuilder.singleton(EventEntity.PROJECT, 1)
        );
        String message = service.getEventMessage(
                Locale.ENGLISH,
                event
        );
        assertEquals("Branch <span class=\"event-entity\">MyBranch</span> has been created for project <a class=\"event-entity\" href=\"project/1\">MyProject</a>.", message);
    }

    @Test
    public void message_branch_deleted_and_project_deleted() {
        EventService eventService = mock(EventService.class);
        Strings strings = new CoreConfig().strings();
        GUIEventService service = new GUIEventServiceImpl(eventService, strings);
        Event event = new Event(
                10,
                EventCode.BRANCH_DELETED,
                Arrays.asList(
                        "MyBranch",
                        "MyProject"
                ),
                new Signature(
                        11,
                        "Damien",
                        new DateTime(2013, 8, 7, 12, 15, DateTimeZone.UTC)
                ),
                Collections.<EventEntity, Integer>emptyMap()
        );
        String message = service.getEventMessage(
                Locale.ENGLISH,
                event
        );
        assertEquals("Branch <span class=\"event-entity\">MyBranch</span> has been deleted for project <span class=\"event-entity\">MyProject</span>.", message);
    }

}
