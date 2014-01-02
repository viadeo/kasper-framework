// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class HttpEventExposerTest extends BaseHttpExposerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private KasperEventBus eventBus;

    @XKasperUnregistered
    public static class UnknownEvent extends Event {
        private static final long serialVersionUID = 6761261204648630883L;

        public String name;
    }

    @XKasperUnregistered
    public static class AccountCreatedEvent extends Event {
        private static final long serialVersionUID = -6112121621645049559L;

        public String name;
    }

    @XKasperDomain(prefix = "test")
    public static class TestDomain implements Domain { }

    @XKasperEventListener(domain = TestDomain.class)
    public static class AccountCreatedEventListener extends EventListener<AccountCreatedEvent> {

        @Override
        public void handle(AccountCreatedEvent event) { }
    }

    // ------------------------------------------------------------------------

    public HttpEventExposerTest() {
        Locale.setDefault(Locale.US);
    }

    // ------------------------------------------------------------------------

    @Override
    protected HttpEventExposerPlugin createExposerPlugin() {
        return new HttpEventExposerPlugin();
    }

    @Override
    protected DomainBundle getDomainBundle(){
        return new DefaultDomainBundle(
                Lists.<CommandHandler>newArrayList(),
                Lists.<QueryHandler>newArrayList(),
                Lists.<Repository>newArrayList(),
                Lists.<EventListener>newArrayList(new AccountCreatedEventListener()),
                Lists.<QueryInterceptorFactory>newArrayList(),
                Lists.<CommandInterceptorFactory>newArrayList(),
                new TestDomain(),
                "TestDomain"
        );
    }

    @Override
    protected void buildPlatform(final PlatformConfiguration platformConfiguration,
                                 final HttpExposerPlugin httpExposerPlugin,
                                 final DomainBundle domainBundle){
        eventBus = spy(platformConfiguration.eventBus());
        new Platform.Builder(platformConfiguration)
                .withEventBus(eventBus)
                .addPlugin(httpExposerPlugin)
                .addDomainBundle(domainBundle)
                .build();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testPublish() throws Exception {
        // Given
        final AccountCreatedEvent event = new AccountCreatedEvent();
        event.name = "tutu";

        // When
        client().emit(DefaultContextBuilder.get(), event);

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
        client().emit(DefaultContextBuilder.get(), event);
    }

}
