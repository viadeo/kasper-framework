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

import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_USER_ID;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KasperClientEventTest {

    private KasperClient client;

    private Client jerseyClient;

    @Captor
    @SuppressWarnings("unused")
    private ArgumentCaptor<ClientRequest> requestArgumentCaptor;

    public static class MemberCreatedEvent extends Event {
        private static final long serialVersionUID = -2618953642539379331L;
        private String name;

        public MemberCreatedEvent(String name) {
            this.name = name;
        }
    }


    @Before
    public void setup() throws IOException {

        Client object = new Client();
        jerseyClient = spy(object);

        client = new KasperClientBuilder()
                .client(jerseyClient)
                .eventBaseLocation(new URL("http://localhost:8080/kasper/event/"))
                .create();
    }


    @Test
    public void testSendEvent() {

        // Given
        final MemberCreatedEvent event = new MemberCreatedEvent("toto");
        doReturn(mock(ClientResponse.class)).when(jerseyClient).handle(requestArgumentCaptor.capture());

        // When
        client.send(DefaultContextBuilder.get().setUserId("boo"), event);

        // Then
        ClientRequest value = requestArgumentCaptor.getValue();
        Assert.assertEquals(URI.create("http://localhost:8080/kasper/event/memberCreated"), value.getURI());
        MultivaluedMap<String, Object> headers = value.getHeaders();
        Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.CONTENT_TYPE).toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.ACCEPT).toString());
        Assert.assertEquals("boo", headers.getFirst(HEADER_USER_ID).toString());
        Assert.assertEquals(event, value.getEntity());
    }

    @Test
    public void testSendEventWithFailureWillRetry3Times() {

        try {
            client.send(DefaultContextBuilder.get(), new MemberCreatedEvent("toto"));
            Assert.fail("exception not raised");
        } catch (KasperException e) {
            Assert.assertEquals("Unable to send event : com.viadeo.kasper.client.KasperClientEventTest$MemberCreatedEvent", e.getMessage());
            Assert.assertEquals("Connection retries limit exceeded.", e.getCause().getMessage());
        }
    }


    @Test
    public void testSendEventWithError() {

        for (ClientResponse.Status status : Arrays.asList(
                ClientResponse.Status.NOT_FOUND,
                ClientResponse.Status.BAD_REQUEST,
                ClientResponse.Status.INTERNAL_SERVER_ERROR

        )) {
            ClientResponse response = mock(ClientResponse.class);
            when(response.getClientResponseStatus()).thenReturn(status);
            doReturn(response).when(jerseyClient).handle(any(ClientRequest.class));
            try {
                client.send(DefaultContextBuilder.get(), new MemberCreatedEvent("toto"));
                Assert.fail("exception not raised");
            } catch (KasperException e) {
                Assert.assertEquals("event submission failed with status <" + status.getReasonPhrase() + ">", e.getCause().getMessage());
            }
        }


    }
}
