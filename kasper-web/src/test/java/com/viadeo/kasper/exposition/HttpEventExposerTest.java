// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class HttpEventExposerTest extends BaseHttpExposerTest<HttpEventExposer> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private KasperEventBus eventBus;


    public static class UnknownEvent extends Event {
        public String name;
    }

    public static class AccountCreatedEvent extends Event {
        public String name;
    }

    public HttpEventExposerTest() {
        Locale.setDefault(Locale.US);
    }

    @Override
    protected HttpEventExposer createExposer(final ApplicationContext ctx) {
        eventBus = spy(ctx.getBean(KasperEventBus.class));
        return new HttpEventExposer(eventBus, Arrays.<Class<? extends Event>>asList(AccountCreatedEvent.class), new HttpContextDeserializer(), ObjectMapperProvider.INSTANCE.mapper());
    }

    @Test
    public void testPublish() throws Exception {
        // Given
        final AccountCreatedEvent event = new AccountCreatedEvent();
        event.name = "tutu";

        // When
        client().send(DefaultContextBuilder.get(), event);

        // Then
        verify(eventBus).publish(event);
    }

    @Test
    public void testPublishWithUnknownEvent() throws Exception {

        // Given valid input
        final Event event = new UnknownEvent();

        // Then ;)
        exception.expect(KasperException.class);
        exception.expectMessage("Unable to send event");

        // When
        client().send(DefaultContextBuilder.get(), event);

    }
}