package com.viadeo.kasper.exposition;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.event.CommandEventListener;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.QueryEventListener;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpEventExposerUTest {

    public static class AEvent extends Event {
        private static final long serialVersionUID = -3423712338104227083L;
    }

    public static class EventListenerA extends CommandEventListener<AEvent> { }

    public static class EventListenerB extends QueryEventListener<AEvent> { }

    @Test
    public void init_withTwoEventListeners_listeningTheSameEvent_isOk() throws Exception {
        // Given
        final List<ExposureDescriptor<Event, EventListener>> descriptors = Lists.newArrayList(
                new ExposureDescriptor<Event, EventListener>(AEvent.class, EventListenerA.class),
                new ExposureDescriptor<Event, EventListener>(AEvent.class, EventListenerB.class)
        );

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        HttpEventExposer eventExposer = new HttpEventExposer(mock(KasperEventBus.class), descriptors);

        // When
        eventExposer.init(servletConfig);

        // Then is ok
    }
}
