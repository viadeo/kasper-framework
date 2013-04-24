/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import static org.junit.Assert.*;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.ICommandResult.Status;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.cqrs.query.IQueryDTO;

public class KasperClientCommandTest extends JerseyTest {
    private KasperClient client;

    @Path(value = "/")
    public static class DummyResource {
        @Path("/CreateMember")
        @PUT
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public ICommandResult getMember(CreateMemberCommand command) {
            return new KasperCommandResult(command.getStatus());
        }
    }

    public static class TestConfiguration extends DefaultResourceConfig {
        public TestConfiguration() {
            super(DummyResource.class);
            getProviderSingletons().add(new JacksonJsonProvider(new KasperClient.Builder().defaultMapper()));
        }
    }

    public KasperClientCommandTest() throws MalformedURLException {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/kasper/command").build());
        client = new KasperClient.Builder()
                .commandBaseLocation(new URL("http://localhost:9998/kasper/command/"))
                .create();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new HTTPContainerFactory();
    }

    @Test
    public void testSendCommand() {
        assertEquals(Status.REFUSED, client.send(new CreateMemberCommand(Status.REFUSED)).getStatus());
    }

    @Test
    public void testSendQueryAsync() throws MalformedURLException, InterruptedException, ExecutionException {
        assertEquals(Status.ERROR, client.sendAsync(new CreateMemberCommand(Status.ERROR)).get().getStatus());
    }

    @Test
    public void testSendQueryAsyncCallback() throws MalformedURLException, InterruptedException, ExecutionException {
        final CountDownLatch latch = new CountDownLatch(1);
        final CreateMemberCommand command = new CreateMemberCommand(Status.OK);
        client.sendAsync(command, new Callback<ICommandResult>() {
            @Override
            public void done(ICommandResult object) {
                assertEquals(Status.OK, object.getStatus());
                latch.countDown();
            }
        });
        
        latch.await(30, TimeUnit.SECONDS);
    }

    public static class MemberDTO implements IQueryDTO {
        private static final long serialVersionUID = 271800729414361903L;
        private String memberName;
        private List<Integer> ids;

        public MemberDTO() {
        }

        public MemberDTO(String memberName, List<Integer> ids) {
            this.memberName = memberName;
            this.ids = ids;
        }

        public String getMemberName() {
            return this.memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }

        public List<Integer> getIds() {
            return this.ids;
        }

        public void setIds(List<Integer> ids) {
            this.ids = ids;
        }
    }

    public static class CreateMemberCommand implements ICommand {
        private static final long serialVersionUID = -2618953632539379331L;
        private Status status;

        public CreateMemberCommand() {}
        
        public CreateMemberCommand(Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return this.status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }
}
