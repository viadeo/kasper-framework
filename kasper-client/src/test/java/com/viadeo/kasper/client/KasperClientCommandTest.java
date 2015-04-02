// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.viadeo.kasper.KasperResponse.Status;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class KasperClientCommandTest extends JerseyTest {

    private static int port;
    private KasperClient client;
    private HttpContextSerializer contextSerializer;

    private static final String SECURITY_TOKEN = "42-4242-24-2424";
    private static final String ACCESS_TOKEN = "666-666-666";

    // -------------------------------------------------------------------------

    public static class MemberResponse {

        private String memberName;
        private List<Integer> ids;

        // --

        public MemberResponse() {
        }

        public MemberResponse(final String memberName, final List<Integer> ids) {
            this.memberName = memberName;
            this.ids = ids;
        }

        // --

        public String getMemberName() {
            return this.memberName;
        }

        public void setMemberName(final String memberName) {
            this.memberName = memberName;
        }

        public List<Integer> getIds() {
            return this.ids;
        }

        public void setIds(final List<Integer> ids) {
            this.ids = ids;
        }
    }

    // --

    public static class CreateMemberCommand implements Command {
        private static final long serialVersionUID = -2618953642539379331L;
        private Status status;

        public CreateMemberCommand() {
        }

        public CreateMemberCommand(final Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return this.status;
        }

        public void setStatus(final Status status) {
            this.status = status;
        }
    }

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void init() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
    }

    @Path(value = "/")
    public static class DummyResource {
        @Path("/createMember")
        @PUT
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public Response getMember(final CreateMemberCommand command) {
            return new ResponseBuilderImpl()
                    .entity(
                            new CommandResponse(
                                    command.getStatus(),
                                    Status.OK != command.getStatus() ? new KasperReason("", "") : null
                            )
                    )
                    .status(200)
                    .header(HttpContextHeaders.HEADER_SECURITY_TOKEN, SECURITY_TOKEN)
                    .header(HttpContextHeaders.HEADER_ACCESS_TOKEN, ACCESS_TOKEN)
                    .build();
        }
    }

    public static class TestConfiguration extends DefaultResourceConfig {
        public TestConfiguration() {
            super(DummyResource.class);
            add(new Application() {
                @Override
                public Set<Object> getSingletons() {
                    return ImmutableSet.of((Object) new JacksonJsonProvider(
                            new KasperClientBuilder().defaultMapper()));
                }
            });
        }
    }

    @Override
    protected int getPort(final int defaultPort) {
        return port;
    }

    // -------------------------------------------------------------------------

    public KasperClientCommandTest() throws IOException {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath(
                "/kasper/command").build());

        contextSerializer = spy(new HttpContextSerializer());

        client = new KasperClientBuilder()
                .contextSerializer(contextSerializer)
                .commandBaseLocation(
                        new URL("http://localhost:" + port + "/kasper/command/")
                )
                .create();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new HTTPContainerFactory();
    }

    // -------------------------------------------------------------------------

    @Test
    public void testSendCommand() {

        // Given
        final CreateMemberCommand command = new CreateMemberCommand(Status.REFUSED);
        final Context context = DefaultContextBuilder.get();

        // When
        final CommandResponse response = client.send(context, command);

        // Then
        assertEquals(Status.REFUSED, response.getStatus());
        assertTrue(response.getSecurityToken().isPresent());
        assertEquals(SECURITY_TOKEN, response.getSecurityToken().get());
        assertTrue(response.getAccessToken().isPresent());
        assertEquals(ACCESS_TOKEN, response.getAccessToken().get());
        verify(contextSerializer).serialize(eq(context), any(AsyncWebResource.Builder.class));
    }

    // --

    @Test
    public void testSendCommandAsync() throws MalformedURLException, InterruptedException,
            ExecutionException {

        // Given
        final CreateMemberCommand command = new CreateMemberCommand(Status.ERROR);
        final Context context = DefaultContextBuilder.get();

        // When
        final Future<? extends CommandResponse> response = client.sendAsync(context, command);

        // Then
        assertEquals(Status.ERROR, response.get().getStatus());
        verify(contextSerializer).serialize(eq(context), any(AsyncWebResource.Builder.class));
    }

    @Test
    public void testSendCommandAsyncCallback() throws MalformedURLException, InterruptedException,
            ExecutionException {

        // Given
        final CountDownLatch latch = new CountDownLatch(1);
        final CreateMemberCommand command = new CreateMemberCommand(Status.OK);
        final Callback<CommandResponse> callback = spy(new Callback<CommandResponse>() {
            @Override
            public void done(CommandResponse object) {
                latch.countDown();
            }
        });
        final ArgumentCaptor<CommandResponse> response = ArgumentCaptor.forClass(CommandResponse.class);
        final Context context = DefaultContextBuilder.get();

        // When
        client.sendAsync(context, command, callback);

        // Then
        latch.await(30, TimeUnit.SECONDS);
        verify(callback).done(response.capture());
        assertEquals(Status.OK, response.getValue().getStatus());
        verify(contextSerializer).serialize(eq(context), any(AsyncWebResource.Builder.class));
    }

    @Test
    public void send_withResultNot200_shouldFillErrorsInResponse() {
        // Given
        final CreateMemberCommand command = new CreateMemberCommand(Status.REFUSED);
        try {
            client = new KasperClientBuilder().commandBaseLocation(new URL("http://localhost:" + port + "/404/")).create();
        } catch (MalformedURLException e) {
            Assert.fail("Shouldn't fail here");
        }

        // When
        final CommandResponse response = client.send(DefaultContextBuilder.get(), command);

        // Then
        assertNotNull(response);
        assertNotNull(response.getReason());
        Assert.assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), response.getReason().getCode());
        Assert.assertEquals(Response.Status.NOT_FOUND, response.asHttp().getHTTPStatus());
        Assert.assertEquals(TransportMode.HTTP, response.getTransportMode());
    }

    @Test
    public void sendAsync_withResultNot200_shouldFillErrorsInResponse() throws MalformedURLException, InterruptedException,
            ExecutionException {

        // Given
        client = new KasperClientBuilder().commandBaseLocation(new URL("http://localhost:" + port + "/404/")).create();
        final CreateMemberCommand command = new CreateMemberCommand(Status.ERROR);

        // When
        final CommandResponse response = client.sendAsync(DefaultContextBuilder.get(), command).get();

        // Then
        assertNotNull(response);
        assertNotNull(response.getReason());
        Assert.assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), response.getReason().getCode());
        Assert.assertEquals(Response.Status.NOT_FOUND, response.asHttp().getHTTPStatus());
        Assert.assertEquals(TransportMode.HTTP, response.getTransportMode());
    }

}
