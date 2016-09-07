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
package com.viadeo.kasper.client;

import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.jersey.api.client.ClientResponse.Status;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KasperClientEventTest {

    private static final String HTTP_HOST = "http://localhost";
    private static final String HTTP_PATH = "/kasper/event/";

    private static int port;

    private KasperClient client;
    private HttpContextSerializer contextSerializer;
    private Client jerseyClient;

    @Captor
    @SuppressWarnings("unused")
    private ArgumentCaptor<ClientRequest> requestArgumentCaptor;

    // ------------------------------------------------------------------------

    public static class MemberCreatedEvent implements Event {
        private String name;

        public MemberCreatedEvent(final String name) {
            this.name = checkNotNull(name);
        }

        public String getName() {
            return name;
        }
    }

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void init() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
    }

    @Before
    public void setup() throws IOException {

        final Client object = new Client();
        jerseyClient = spy(object);

        contextSerializer = spy(new HttpContextSerializer());

        client = new KasperClientBuilder()
                    .client(jerseyClient)
                    .contextSerializer(contextSerializer)
                    .eventBaseLocation(new URL(HTTP_HOST + ":" + port + HTTP_PATH))
                    .create();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    @Test
    public void testSendEvent() {

        // Given
        final MemberCreatedEvent event = new MemberCreatedEvent("toto");
        final ClientResponse response = mock(ClientResponse.class);
        doReturn(Status.ACCEPTED).when(response).getClientResponseStatus();
        doReturn(response).when(jerseyClient).handle(requestArgumentCaptor.capture());
        final Context context = Contexts.builder().withUserId("boo").build();

        // When
        client.emit(context, event);

        // Then
        final ClientRequest value = requestArgumentCaptor.getValue();
        assertEquals(URI.create(HTTP_HOST + ":" + port + HTTP_PATH + "memberCreated"), value.getURI());

        final MultivaluedMap<String, Object> headers = value.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.CONTENT_TYPE).toString());
        assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.ACCEPT).toString());
        assertEquals("boo", headers.getFirst(HttpContextHeaders.HEADER_USER_ID.toHeaderName()).toString());
        assertEquals(event, value.getEntity());
        verify(contextSerializer).serialize(eq(context), any(AsyncWebResource.Builder.class));
    }

    @Test
    public void testSendEventWithFailureWillRetry3Times() {
        // Given

        try {
            // When
            client.emit(Contexts.empty(), new MemberCreatedEvent("toto"));
            Assert.fail("exception not raised");

        } catch (final KasperException e) {

            // Then
            assertEquals("Unable to send event : " + MemberCreatedEvent.class.getName(), e.getMessage());
            assertEquals("Connection retries limit exceeded.", e.getCause().getMessage());
        }
    }


    @Test
    public void testSendEventWithError() {

        final List<Status> statuses = Arrays.asList(
                Status.NOT_FOUND,
                Status.BAD_REQUEST,
                Status.INTERNAL_SERVER_ERROR
        );

        for (final Status status : statuses) {
            // Given
            final ClientResponse response = mock(ClientResponse.class);
            when(response.getClientResponseStatus()).thenReturn(status);
            doReturn(response).when(jerseyClient).handle(any(ClientRequest.class));

            try {
                // When
                client.emit(Contexts.empty(), new MemberCreatedEvent("toto"));
                Assert.fail("exception not raised");

            } catch (final KasperException e) {

                // Then
                assertEquals("event submission failed with status <" + status.getReasonPhrase() + ">", e.getCause().getMessage());
            }
        }
    }

}
