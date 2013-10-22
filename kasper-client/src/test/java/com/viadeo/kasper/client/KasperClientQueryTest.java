// ============================================================================
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.cqrs.TransportMode;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class KasperClientQueryTest extends JerseyTest {

    private static int port;
    private KasperClient client;

    // ------------------------------------------------------------------------

    public static class MemberResult implements QueryResult {
        private static final long serialVersionUID = 5377830561251071588L;
        
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

    public static class GetMemberQuery implements Query {
        private static final long serialVersionUID = -2618953632539379331L;

        private String memberName;
        private List<Integer> ids;

        public GetMemberQuery() {
        }

        public GetMemberQuery(final String memberName, final List<Integer> ids) {
            this.memberName = memberName;
            this.ids = ids;
        }

        public String getMemberName() {
            return this.memberName;
        }

        public List<Integer> getIds() {
            return this.ids;
        }

        public void setIds(List<Integer> ids) {
            this.ids = ids;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
    }

    // ------------------------------------------------------------------------

    @Path(value = "/getMember")
    public static class DummyResource {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public MemberResult getMember(@QueryParam("memberName") final String memberName,
                                      @QueryParam("ids") final List<Integer> ids) {
            return new MemberResult(memberName, ids);
        }

        @POST
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public MemberResult getPostMember(ImmutableSetMultimap<String, String> query) {
            return new MemberResult(query.get("memberName").iterator().next(), Lists.newArrayList(Iterables.transform(query.get("ids"), new Function<String, Integer>() {
                @Override
                public Integer apply(String input) {
                    return Integer.parseInt(input);
                }
            })));
        }
    }
    
    @BeforeClass
    public static void init() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
    }

    // ------------------------------------------------------------------------

    private void checkRoundTrip(final GetMemberQuery original, final QueryResponse<MemberResult> obtained) {
        assertEquals(original.getMemberName(), obtained.getResult().getMemberName());

        final List<Integer> expected = original.getIds();
        final List<Integer> actual = obtained.getResult().getIds();
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    // ------------------------------------------------------------------------

    public static class TestConfiguration extends DefaultResourceConfig {
        public TestConfiguration() {
            super(DummyResource.class);
        }

        @Override
        public Set<Object> getSingletons() {
            return Sets.<Object>newHashSet(new JacksonJsonProvider(ObjectMapperProvider.INSTANCE.mapper()));
        }
    }

    public KasperClientQueryTest() throws IOException {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/kasper/query").build());
        
        client = new KasperClientBuilder()
                .queryBaseLocation(new URL("http://localhost:" + port + "/kasper/query/"))
                .create();
    }

    @Override
    protected int getPort(final int defaultPort) {
        return port;
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new HTTPContainerFactory();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSendQuery() {

        // Given
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));

        // When
        final QueryResponse<MemberResult> response = client.query(query, MemberResult.class);

        // Then
        checkRoundTrip(query, response);
    }

    @Test
    public void testSendQueryAsync() throws MalformedURLException, InterruptedException, ExecutionException {

        // Given
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));

        // When 
        final QueryResponse<MemberResult> response = client.queryAsync(query, MemberResult.class).get();

        // Then
        checkRoundTrip(query, response);
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testSendQueryAsyncCallback() throws MalformedURLException, InterruptedException, ExecutionException {

        // Given
        final CountDownLatch latch = new CountDownLatch(1);
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));
        final Callback<QueryResponse<MemberResult>> callback = spy(new Callback<QueryResponse<MemberResult>>() {
            @Override
            public void done(QueryResponse<MemberResult> response) {
                latch.countDown();
            }
        });
        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<QueryResponse> response = ArgumentCaptor.forClass(QueryResponse.class);

        // When
        client.queryAsync(query, MemberResult.class, callback);

        // Then
        latch.await(5, TimeUnit.SECONDS);
        verify(callback).done(response.capture());
        checkRoundTrip(query, response.getValue());
    }

    @Test public void testQueryUsingPost() throws MalformedURLException {
        // Given
        final KasperClient client = new KasperClientBuilder()
                .queryBaseLocation(new URL("http://localhost:" + port + "/kasper/query/"))
                .usePostForQueries(true)
                .create();
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));

        // When
        final QueryResponse<MemberResult> response = client.query(query, MemberResult.class);

        // Then
        checkRoundTrip(query, response);
    }

    @Test public void query_withResultNot200_shouldFillErrorsInResponse() {
        // Given
        KasperClient client = null;
        try {
            client = new KasperClientBuilder()
                    .queryBaseLocation(new URL("http://localhost:" + port + "/404/"))
                    .create();
        } catch (MalformedURLException e) {
            Assert.fail("Shouldn't throw exception here");
        }
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));

        // When
        final QueryResponse<MemberResult> response = client.query(query, MemberResult.class);

        // Then
        Assert.assertEquals(CoreReasonCode.UNKNOWN_REASON.toString(), response.getReason().getCode());
        Assert.assertEquals(Response.Status.NOT_FOUND, response.asHttp().getHTTPStatus());
        Assert.assertEquals(TransportMode.HTTP, response.getTransportMode());
    }

    @Test public void queryAsync_withResultNot200_shouldFillErrorsInResponse() throws MalformedURLException, InterruptedException, ExecutionException {
        // Given
        client = new KasperClientBuilder()
                .queryBaseLocation(new URL("http://localhost:" + port + "/404/"))
                .create();
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));

        // When
        final QueryResponse<MemberResult> response = client.queryAsync(query, MemberResult.class).get();

        // Then
        Assert.assertEquals(CoreReasonCode.UNKNOWN_REASON.toString(), response.getReason().getCode());
        Assert.assertEquals(Response.Status.NOT_FOUND, response.asHttp().getHTTPStatus());
        Assert.assertEquals(TransportMode.HTTP, response.getTransportMode());
    }
}
