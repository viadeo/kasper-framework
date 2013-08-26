// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.client.lib.Callback;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.command.CommandResult.Status;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class KasperClientCommandTest extends JerseyTest {

    private static int port;
    private KasperClient client;

    // -------------------------------------------------------------------------

    public static class MemberResult {

        private String memberName;
        private List<Integer> ids;

        // --

        public MemberResult() {
        }

        public MemberResult(final String memberName, final List<Integer> ids) {
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
        public CommandResult getMember(final CreateMemberCommand command) {
            return new CommandResult(command.getStatus(),
                    Status.OK != command.getStatus() ? new KasperError("", "") : null);
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
        client = new KasperClientBuilder().commandBaseLocation(
                new URL("http://localhost:" + port + "/kasper/command/")).create();
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

        // When
        final CommandResult result = client.send(command);

        // Then
        assertEquals(Status.REFUSED, result.getStatus());
    }

    // --

    @Test
    public void testSendCommandAsync() throws MalformedURLException, InterruptedException,
            ExecutionException {

        // Given
        final CreateMemberCommand command = new CreateMemberCommand(Status.ERROR);

        // When
        final Future<? extends CommandResult> result = client.sendAsync(command);

        // Then
        assertEquals(Status.ERROR, result.get().getStatus());
    }

    @Test
    public void testSendCommandAsyncCallback() throws MalformedURLException, InterruptedException,
            ExecutionException {

        // Given
        final CountDownLatch latch = new CountDownLatch(1);
        final CreateMemberCommand command = new CreateMemberCommand(Status.OK);
        final Callback<CommandResult> callback = spy(new Callback<CommandResult>() {
            @Override
            public void done(CommandResult object) {
                latch.countDown();
            }
        });
        final ArgumentCaptor<CommandResult> result = ArgumentCaptor.forClass(CommandResult.class);

        // When
        client.sendAsync(command, callback);

        // Then
        latch.await(30, TimeUnit.SECONDS);
        verify(callback).done(result.capture());
        assertEquals(Status.OK, result.getValue().getStatus());
    }

}
