// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.base.Throwables;
import com.sun.jersey.api.client.*;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.header.MediaTypes;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.CommandResponse.Status;
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

import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_USER_ID;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KasperClientEventTest  {

    private KasperClient client;

    private Client jerseyClient;

    @Captor
    private ArgumentCaptor<ClientRequest> requestArgumentCaptor;

    public static class MemberCreatedEvent extends Event {
        private static final long serialVersionUID = -2618953642539379331L;
        private Status status;

        public MemberCreatedEvent(Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return this.status;
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
        final MemberCreatedEvent event = new MemberCreatedEvent(Status.REFUSED);
        doReturn(mock(ClientResponse.class)).when(jerseyClient).handle(requestArgumentCaptor.capture());

        // When
        client.send(DefaultContextBuilder.get().setUserId("boo"), event);

        // Then
        ClientRequest value = requestArgumentCaptor.getValue();
        Assert.assertEquals(URI.create("http://localhost:8080/kasper/event/memberCreated"), value.getURI());
        MultivaluedMap<String,Object> headers = value.getHeaders();
        Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.CONTENT_TYPE).toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getFirst(HttpHeaders.ACCEPT).toString());
        Assert.assertEquals("boo", headers.getFirst(HEADER_USER_ID).toString());
        Assert.assertEquals(event, value.getEntity());
    }



    @Test(expected = KasperException.class)
    public void testSendEventWithFailureWillRetry3Times() {

        try {
            client.send(DefaultContextBuilder.get(), new MemberCreatedEvent(Status.ERROR));
        } catch (KasperException e) {
            Assert.assertEquals("Connection retries limit exceeded.", e.getCause().getMessage());
            throw Throwables.propagate(e);
        }
    }
}
