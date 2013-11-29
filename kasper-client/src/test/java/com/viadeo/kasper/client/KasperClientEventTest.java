// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.exception.KasperException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.jersey.api.client.ClientResponse.Status;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_USER_ID;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KasperClientEventTest {

    private static final String HTTP_ENDPOINT = "http://localhost:8080/kasper/event/";

    private KasperClient client;
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

    @Before
    public void setup() throws IOException {

        final Client object = new Client();
        jerseyClient = spy(object);

        client = new KasperClientBuilder()
                    .client(jerseyClient)
                    .eventBaseLocation(new URL(HTTP_ENDPOINT))
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

        // When
        client.emit(DefaultContextBuilder.get().setUserId("boo"), event);

        // Then
        final ClientRequest value = requestArgumentCaptor.getValue();
        assertEquals(URI.create(HTTP_ENDPOINT + "memberCreated"), value.getURI());

        final MultivaluedMap<String, Object> headers = value.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.CONTENT_TYPE).toString());
        assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.ACCEPT).toString());
        assertEquals("boo", headers.getFirst(HEADER_USER_ID).toString());
        assertEquals(event, value.getEntity());
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
