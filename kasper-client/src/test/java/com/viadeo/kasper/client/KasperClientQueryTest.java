/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.viadeo.kasper.client.lib.ICallback;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;

public class KasperClientQueryTest extends JerseyTest {
    private KasperClient client;

    @Path(value = "/")
    public static class DummyResource {
        @Path("/GetMember")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public MemberDTO getMember(@QueryParam("memberName") String memberName, @QueryParam("ids") List<Integer> ids) {
            return new MemberDTO(memberName, ids);
        }
    }

    public static class TestConfiguration extends DefaultResourceConfig {
        public TestConfiguration() {
            super(DummyResource.class);
            getProviderSingletons().add(new JacksonJsonProvider());
        }
    }

    public KasperClientQueryTest() throws MalformedURLException {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/kasper/query").build());
        client = new KasperClientBuilder()
                .client(client())
                .queryBaseLocation(new URL("http://localhost:9998/kasper/query/"))
                .create();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new HTTPContainerFactory();
    }

    @Test
    public void testSendQuery() {
        GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));
        MemberDTO dto = client.query(query, MemberDTO.class);
        checkRoundTrip(query, dto);
    }

    @Test
    public void testSendQueryAsync() throws MalformedURLException, InterruptedException, ExecutionException {
        GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));
        MemberDTO dto = client.queryAsync(query, MemberDTO.class).get();
        checkRoundTrip(query, dto);
    }

    @Test
    public void testSendQueryAsyncCallback() throws MalformedURLException, InterruptedException, ExecutionException {
        final CountDownLatch latch = new CountDownLatch(1);
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));
        client.queryAsync(query, MemberDTO.class, new ICallback<MemberDTO>() {
            @Override
            public void done(MemberDTO object) {
                checkRoundTrip(query, object);
                latch.countDown();
            }
        });
        
        latch.await(30, TimeUnit.SECONDS);
    }

    private void checkRoundTrip(GetMemberQuery original, MemberDTO obtained) {
        assertEquals(original.getMemberName(), obtained.getMemberName());
        List<Integer> expected = original.getIds();
        List<Integer> actual = obtained.getIds();
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
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

    public static class GetMemberQuery implements IQuery {
        private static final long serialVersionUID = -2618953632539379331L;
        private final String memberName;
        private final List<Integer> ids;

        public GetMemberQuery(String memberName, List<Integer> ids) {
            this.memberName = memberName;
            this.ids = ids;
        }

        public String getMemberName() {
            return this.memberName;
        }

        public List<Integer> getIds() {
            return this.ids;
        }
    }
}
