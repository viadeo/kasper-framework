// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.component.event.CommandEventListener;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.event.QueryEventListener;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpEventExposerUTest {

    public static class AEvent implements Event {
        private static final long serialVersionUID = -3423712338104227083L;
    }

    public static class EventListenerA extends CommandEventListener<AEvent> {
        @Override
        public EventResponse handle(Context context, AEvent event) {
            return EventResponse.success();
        }
    }

    public static class EventListenerB extends QueryEventListener<AEvent> {
        @Override
        public EventResponse handle(Context context, AEvent event) {
            return EventResponse.success();
        }
    }

    @Test
    public void init_withTwoEventListeners_listeningTheSameEvent_isOk() throws Exception {
        // Given
        final List<ExposureDescriptor<Event, EventListener>> descriptors = Lists.newArrayList();
        descriptors.add(new ExposureDescriptor<Event, EventListener>(AEvent.class, EventListenerA.class));
        descriptors.add(new ExposureDescriptor<Event, EventListener>(AEvent.class, EventListenerB.class));

        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        final ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        final Platform platform = mock(Platform.class);
        when(platform.getEventBus()).thenReturn(mock(KasperEventBus.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpEventExposer eventExposer = new HttpEventExposer(platform, descriptors);

        // When
        eventExposer.init(servletConfig);

        // Then is ok
    }
}
