// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import com.viadeo.kasper.client.lib.ICallback;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.ICommandResult.Status;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.cqrs.query.IQueryDTO;


public class KasperClientCommandTest extends JerseyTest {
	
    private static int port;
    
    private KasperClient client;
    
    //-------------------------------------------------------------------------
    
    public static class MemberDTO implements IQueryDTO {
        private static final long serialVersionUID = 271800729414361903L;
        private String memberName;
        private List<Integer> ids;

        public MemberDTO() { }

        public MemberDTO(final String memberName, final List<Integer> ids) {
            this.memberName = memberName;
            this.ids = ids;
        }

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

    public static class CreateMemberCommand implements ICommand {
        private static final long serialVersionUID = -2618953632539379331L;
        private Status status;

        public CreateMemberCommand() {}
        
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
    
    @BeforeClass public static void init() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
    }
    
    @Path(value = "/")
    public static class DummyResource {
        @Path("/createMember")
        @PUT
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public ICommandResult getMember(final CreateMemberCommand command) {
            return new KasperCommandResult(command.getStatus());
        }
    }

    public static class TestConfiguration extends DefaultResourceConfig {
        public TestConfiguration() {
            super(DummyResource.class);
            getProviderSingletons().add(new JacksonJsonProvider(new KasperClientBuilder().defaultMapper()));
        }
    }

    @Override
    protected int getPort(int defaultPort) {
        return port;
    }
    
    //-------------------------------------------------------------------------
    
    public KasperClientCommandTest() throws MalformedURLException {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/kasper/command").build());
        client = new KasperClientBuilder()
                .commandBaseLocation(new URL("http://localhost:"+port+"/kasper/command/"))
                .create();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new HTTPContainerFactory();
    }

    //-------------------------------------------------------------------------
    
    @Test
    public void testSendCommand() {
    	
    	//Given
    	final CreateMemberCommand command = new CreateMemberCommand(Status.REFUSED);
    	
    	// When
    	final ICommandResult result = client.send(command);
    	
    	// Then
        assertEquals(Status.REFUSED, result.getStatus());
    }

    // --
    
    @Test
    public void testSendQueryAsync() throws MalformedURLException, InterruptedException, ExecutionException {
    	
    	// Given
    	final CreateMemberCommand command = new CreateMemberCommand(Status.ERROR);
    	
    	// When
    	final Future<? extends ICommandResult> result = client.sendAsync(command);
    	
    	// Then
        assertEquals(Status.ERROR, result.get().getStatus());
    }

    @Test
    public void testSendQueryAsyncCallback() throws MalformedURLException, InterruptedException, ExecutionException {
    	
    	// Given 
        final CountDownLatch latch = new CountDownLatch(1);
        final CreateMemberCommand command = new CreateMemberCommand(Status.OK);
        final ICallback<ICommandResult> callback = spy(new ICallback<ICommandResult>() {
            @Override
            public void done(ICommandResult object) {
                latch.countDown();
            }
        });
        final ArgumentCaptor<ICommandResult> result = ArgumentCaptor.forClass(ICommandResult.class);
        
        // When
        client.sendAsync(command, callback);
        
        // Then
        latch.await(30, TimeUnit.SECONDS);
        verify(callback).done(result.capture());
        assertEquals(Status.OK, result.getValue().getStatus());
    }
    
}
