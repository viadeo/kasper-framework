// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.annotation.XKasperAlias;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
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
import com.viadeo.kasper.event.EventResponse;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.exposition.http.HttpEventExposerPlugin;
import com.viadeo.kasper.exposition.http.HttpExposerPlugin;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.*;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class HttpEventExposerTest extends BaseHttpExposerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private KasperEventBus eventBus;

    @XKasperUnregistered
    public static class AccountUpdatedEvent implements Event {
        private static final long serialVersionUID = -6112121621645049559L;
    }

    @XKasperUnregistered
    public static class AccountCreatedEvent implements Event {
        private static final long serialVersionUID = -6112121621645049559L;
        public String name;
    }

    @XKasperDomain(prefix = "test")
    public static class TestDomain implements Domain { }

    @XKasperEventListener(domain = TestDomain.class)
    public static class AccountCreatedEventListener extends EventListener<AccountCreatedEvent> {
        @Override
        public EventResponse handle(Context context, AccountCreatedEvent event) {
            return EventResponse.success();
        }
    }

    public static final String NEED_VALIDATION_2_ALIAS = "needvalidation2";

    @XKasperUnregistered
    @XKasperAlias(values = {NEED_VALIDATION_2_ALIAS})
    public static class NeedValidationEvent implements Event {
        private static final long serialVersionUID = -8918994635071831597L;
    }

    @XKasperEventListener(domain = TestDomain.class)
    public static class NeedValidationEventListener extends EventListener<NeedValidationEvent> {
        @Override
        public EventResponse handle(Context context, NeedValidationEvent event) {
            return EventResponse.success();
        }
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
                Lists.<EventListener>newArrayList(new AccountCreatedEventListener(), new NeedValidationEventListener()),
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
        verify(eventBus).publishEvent(any(Context.class), refEq(event));
    }

    @Test
    public void testPublishWithUnknownEvent() throws Exception {

        // Given valid input
        final Event event = new Event() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };

        // Then ;)
        exception.expect(KasperException.class);
        exception.expectMessage("Unable to send event");

        // When
        client().emit(DefaultContextBuilder.get(), event);
    }

    @Test
    public void testNonListenedEvent() throws MalformedURLException, URISyntaxException {
        // Given
        final Event event = new AccountUpdatedEvent();

        // When
        client().emit(DefaultContextBuilder.get(), event);

        // Then
        verify(eventBus).publishEvent(any(Context.class), refEq(event));
    }

    @Test
    public void testAliasedEvent() throws MalformedURLException, URISyntaxException {
        // Given
        final String eventPath = NEED_VALIDATION_2_ALIAS;
        final NeedValidationEvent needValidationWithAlias = new NeedValidationEvent();

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), eventPath).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class, needValidationWithAlias);

        // Then
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testXKasperServerNameInHeader() throws MalformedURLException, URISyntaxException, UnknownHostException {
        // Given
        final String expectedServerName = InetAddress.getLocalHost().getCanonicalHostName();
        final AccountCreatedEvent event = new AccountCreatedEvent();

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), event.getClass().getSimpleName().replace("Event", "")).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class, event);

        // Then
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        assertEquals(expectedServerName, response.getHeaders().getFirst(HttpContextHeaders.HEADER_SERVER_NAME));
    }
}
