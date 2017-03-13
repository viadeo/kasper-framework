// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.builder.DefaultPlatform;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.configuration.PlatformConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.*;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
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
    public static class AccountCreatedEventListener extends AutowiredEventListener<AccountCreatedEvent> {
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

    public static class NeedJSR303ValidationEvent implements Event {
        private static final long serialVersionUID = 5118539819893435486L;
        @NotNull
        @Size(min = 1) public String name;
    }

    @XKasperEventListener(domain = TestDomain.class)
    public static class NeedValidationEventListener extends AutowiredEventListener<NeedValidationEvent> {
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
    protected HttpExposer getHttpExposer() {
        return httpExposurePlugin.getEventExposer();
    }

    @Override
    protected DomainBundle getDomainBundle(){
        return new DefaultDomainBundle(
                Lists.<CommandHandler>newArrayList(),
                Lists.<QueryHandler>newArrayList(),
                Lists.<Repository>newArrayList(),
                Lists.<EventListener>newArrayList(new AccountCreatedEventListener(), new NeedValidationEventListener()),
                Lists.<Saga>newArrayList(),
                Lists.<QueryInterceptorFactory>newArrayList(),
                Lists.<CommandInterceptorFactory>newArrayList(),
                Lists.<EventInterceptorFactory>newArrayList(),
                new TestDomain(),
                "TestDomain"
        );
    }

    @Override
    protected DefaultPlatform.Builder platformBuilder(final PlatformConfiguration platformConfiguration,
                                 final DomainBundle domainBundle){
        eventBus = spy(platformConfiguration.eventBus());
        return super.platformBuilder(platformConfiguration, domainBundle).withEventBus(eventBus);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testPublish() throws Exception {
        // Given
        final AccountCreatedEvent event = new AccountCreatedEvent();
        event.name = "tutu";

        // When
        client().emit(Contexts.empty(), event);

        // Then
        verify(eventBus).publish(any(Context.class), refEq(event));
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
        client().emit(Contexts.empty(), event);
    }

    @Test
    public void testNonListenedEvent() throws Exception {
        // Given
        final Event event = new AccountUpdatedEvent();

        // When
        client().emit(Contexts.empty(), event);

        // Then
        verify(eventBus).publish(any(Context.class), refEq(event));
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
        assertEquals(expectedServerName, response.getHeaders().getFirst(HttpContextHeaders.HEADER_SERVER_NAME.toHeaderName()));
    }

    @Test
    public void testJSR303Validation_withWrongEntries_shouldReturnBadRequest() throws MalformedURLException, URISyntaxException, UnknownHostException {
        // Given
        final NeedJSR303ValidationEvent event = new NeedJSR303ValidationEvent();
        event.name = "";

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), event.getClass().getSimpleName().replace("Event", "")).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class, event);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testJSR303Validation_withGoodEntries_shouldReturnAccepted() throws MalformedURLException, URISyntaxException, UnknownHostException {
        // Given
        final NeedJSR303ValidationEvent event = new NeedJSR303ValidationEvent();
        event.name = "name";

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), event.getClass().getSimpleName().replace("Event", "")).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class, event);

        // Then
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
    }
}
