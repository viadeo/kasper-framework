// ============================================================================
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import com.viadeo.kasper.client.lib.Callback;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
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
        private static final long serialVersionUID = 271800729414361903L;

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

        private final String memberName;
        private final List<Integer> ids;

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
    }

    // ------------------------------------------------------------------------

    @Path(value = "/")
    public static class DummyResource {
        @Path("/getMember")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public MemberResult getMember(@QueryParam("memberName") final String memberName, @QueryParam("ids") final List<Integer> ids) {
            return new MemberResult(memberName, ids);
        }
    }
    
    @BeforeClass
    public static void init() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
    }

    // ------------------------------------------------------------------------

    private void checkRoundTrip(final GetMemberQuery original, final MemberResult obtained) {
        assertEquals(original.getMemberName(), obtained.getMemberName());

        final List<Integer> expected = original.getIds();
        final List<Integer> actual = obtained.getIds();
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    // ------------------------------------------------------------------------

    public static class TestConfiguration extends DefaultResourceConfig {
        public TestConfiguration() {
            super(DummyResource.class);
            getProviderSingletons().add(new JacksonJsonProvider());
        }
    }

    public KasperClientQueryTest() throws IOException {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/kasper/query").build());
        
        client = new KasperClientBuilder()
                .client(client())
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
        final MemberResult result = client.query(query, MemberResult.class);

        // Then
        checkRoundTrip(query, result);
    }

    @Test
    public void testSendQueryAsync() throws MalformedURLException, InterruptedException, ExecutionException {

        // Given
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));

        // When 
        final MemberResult result = client.queryAsync(query, MemberResult.class).get();

        // Then
        checkRoundTrip(query, result);
    }

    @Test
    public void testSendQueryAsyncCallback() throws MalformedURLException, InterruptedException, ExecutionException {

        // Given
        final CountDownLatch latch = new CountDownLatch(1);
        final GetMemberQuery query = new GetMemberQuery("foo bar", Arrays.asList(1, 2, 3));
        final Callback<MemberResult> callback = spy(new Callback<MemberResult>() {
            @Override
            public void done(MemberResult result) {
                latch.countDown();
            }
        });
        final ArgumentCaptor<MemberResult> result = ArgumentCaptor.forClass(MemberResult.class);

        // When
        client.queryAsync(query, MemberResult.class, callback);

        // Then
        latch.await(5, TimeUnit.SECONDS);
        verify(callback).done(result.capture());
        checkRoundTrip(query, result.getValue());
    }

}
