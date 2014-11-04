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
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.exception.KasperException;
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
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_USER_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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

    public static class MemberCreatedEvent extends Event {
        private String name;

        public MemberCreatedEvent(final String name) {
            this.name = checkNotNull(name);
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

    @Test
    public void testSendEvent() {

        // Given
        final MemberCreatedEvent event = new MemberCreatedEvent("toto");
        final ClientResponse response = mock(ClientResponse.class);
        doReturn(Status.ACCEPTED).when(response).getClientResponseStatus();
        doReturn(response).when(jerseyClient).handle(requestArgumentCaptor.capture());
        final Context context = DefaultContextBuilder.get().setUserId("boo");

        // When
        client.emit(context, event);

        // Then
        final ClientRequest value = requestArgumentCaptor.getValue();
        assertEquals(URI.create(HTTP_HOST + ":" + port + HTTP_PATH + "memberCreated"), value.getURI());

        final MultivaluedMap<String, Object> headers = value.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.CONTENT_TYPE).toString());
        assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.ACCEPT).toString());
        assertEquals("boo", headers.getFirst(HEADER_USER_ID).toString());
        assertEquals(event, value.getEntity());
        verify(contextSerializer).serialize(eq(context), any(AsyncWebResource.Builder.class));
    }

    @Test
    public void testSendEventWithFailureWillRetry3Times() {
        // Given

        try {
            // When
            client.emit(DefaultContextBuilder.get(), new MemberCreatedEvent("toto"));
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
                client.emit(DefaultContextBuilder.get(), new MemberCreatedEvent("toto"));
                Assert.fail("exception not raised");

            } catch (final KasperException e) {

                // Then
                assertEquals("event submission failed with status <" + status.getReasonPhrase() + ">", e.getCause().getMessage());
            }
        }
    }

}
