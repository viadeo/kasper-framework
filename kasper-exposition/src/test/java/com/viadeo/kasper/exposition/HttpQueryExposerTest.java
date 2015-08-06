// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.annotation.XKasperUnexposed;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperQueryException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.client.HTTPQueryResponse;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.QueryMessage;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.exposition.http.HttpQueryExposerPlugin;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HttpQueryExposerTest extends BaseHttpExposerTest {

    public static class SomeCollectionQuery extends SomeQuery {
        private static final long serialVersionUID = 104409802777527460L;
    }

    public static class SomeCollectionResponse extends CollectionQueryResult<SomeResponse> {
        private static final long serialVersionUID = 1433643086186132048L;

        public SomeCollectionResponse(Collection<SomeResponse> list) {
            super(list);
        }
    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class SomeCollectionQueryHandler extends QueryHandler<SomeCollectionQuery, SomeCollectionResponse> {
        @Override
        public QueryResponse<SomeCollectionResponse> retrieve(final QueryMessage<SomeCollectionQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();

            final SomeResponse response = new SomeResponse();
            response.setQuery(q);

            final SomeCollectionResponse list = new SomeCollectionResponse(Arrays.asList(response));

            return QueryResponse.of(list);
        }
    }

    public static class SomeQuery implements Query {
        private static final long serialVersionUID = -7447288176593489294L;

        private String aValue;
        private int[] intArray;
        private boolean doThrowSomeException;

        private List<String> errorCodes;

        public int[] getIntArray() {
            return intArray;
        }

        public void setIntArray(final int[] intArray) {
            this.intArray = intArray;
        }

        public String getAValue() {
            return aValue;
        }

        public boolean isDoThrowSomeException() {
            return doThrowSomeException;
        }

        public void setAValue(final String aValue) {
            this.aValue = aValue;
        }

        public void setDoThrowSomeException(final boolean doThrowSomeException) {
            this.doThrowSomeException = doThrowSomeException;
        }

        public List<String> getErrorCodes() {
            return errorCodes;
        }

        public void setErrorCodes(final List<String> errorCodes) {
            this.errorCodes = errorCodes;
        }
    }

    public static class SomeResponse implements QueryResult {
        private SomeQuery query;

        public SomeQuery getQuery() {
            return query;
        }

        public void setQuery(final SomeQuery query) {
            this.query = query;
        }
    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    @SuppressWarnings("unchecked")
    public static class SomeQueryHandler extends QueryHandler<SomeQuery, SomeResponse> {
        @Override
        public QueryResponse<SomeResponse> retrieve(final QueryMessage<SomeQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();

            if (q.isDoThrowSomeException()) {
                final List<String> messages = new ArrayList<>();
                if (q.getErrorCodes() != null) {
                    for (int i = 0; i < q.getErrorCodes().size(); i++)
                        messages.add(q.getErrorCodes().get(i));
                }

                return QueryResponse.error(new KasperReason(q.aValue, messages));
            }

            final SomeResponse response = new SomeResponse();
            response.setQuery(q);
            return QueryResponse.of(response);
        }
    }

    public static final String NEED_VALIDATION_2_ALIAS = "needvalidation2";

    @XKasperAlias(values = {NEED_VALIDATION_2_ALIAS})
    public static class NeedValidationWithAlias implements Query {
        private static final long serialVersionUID = -8083928873466120009L;
    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class NeedValidationWithAliasQueryHandler extends QueryHandler<NeedValidationWithAlias, SomeResponse> {
        @Override
        public QueryResponse<SomeResponse> retrieve(NeedValidationWithAlias query) throws Exception {
            return QueryResponse.of(new SomeResponse());
        }
    }

    public static class UnexposedQuery implements Query {
        private static final long serialVersionUID = -8083928873466120009L;
    }

    @XKasperUnexposed
    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class UnexposedQueryHandler extends QueryHandler<UnexposedQuery, SomeResponse> {
        @Override
        public QueryResponse<SomeResponse> retrieve(UnexposedQuery query) throws Exception {
            return QueryResponse.of(new SomeResponse());
        }
    }

    // ------------------------------------------------------------------------
    private boolean usePostForQueries;

    public HttpQueryExposerTest(final boolean usePostForQueries) {
        this.usePostForQueries = usePostForQueries;
    }

    @Parameterized.Parameters public static Collection<Object[]> params() {
        Object[][] params = new Object[][] { {false}, {true}};
        return Lists.newArrayList(params);
    }

    @Override
    protected void customize(KasperClientBuilder clientBuilder) {
        clientBuilder.usePostForQueries(usePostForQueries);
    }

    // ------------------------------------------------------------------------

    @Override
    protected HttpQueryExposerPlugin createExposerPlugin() {
        return new HttpQueryExposerPlugin();
    }

    @Override
    protected DomainBundle getDomainBundle(){
        return new DomainBundle.Builder(new AccountDomain())
                .with(
                        new SomeQueryHandler(),
                        new SomeCollectionQueryHandler(),
                        new NeedValidationWithAliasQueryHandler(),
                        new UnexposedQueryHandler()
                )
                .build();
    }

    @Test
    public void testQueryRoundTrip() throws JsonProcessingException {
        // Given
        final SomeQuery query = new SomeQuery();
        query.aValue = "foo";
        query.doThrowSomeException = false;
        query.intArray = new int[] { 1, 2, 3 };

        // When
        final QueryResponse<SomeResponse> response = client().query(
                Contexts.empty(), query, SomeResponse.class);

        // Then
        assertEquals(query.aValue, response.getResult().query.aValue);
        assertEquals(query.doThrowSomeException, response.getResult().query.doThrowSomeException);
        assertArrayEquals(query.intArray, response.getResult().query.intArray);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerThrowingException() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = true;
        query.aValue = "aaa";

        // When
        final QueryResponse<SomeResponse> actual = client().query(
                Contexts.empty(), query, SomeResponse.class);

        // Then
        assertFalse(actual.isOK());
        assertEquals(query.aValue, actual.getReason().getLabel());
        Assert.assertTrue(actual instanceof HTTPQueryResponse);

        HTTPQueryResponse httpResponse = (HTTPQueryResponse) actual;
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, httpResponse.getHTTPStatus());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerHttpCode() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = true;
        query.aValue = CoreReasonCode.NOT_FOUND.name();

        // When
        final QueryResponse<SomeResponse> actual = client().query(
                Contexts.empty(), query, SomeResponse.class);

        // Then
        assertFalse(actual.isOK());
        assertEquals(query.aValue, actual.getReason().getLabel());
        assertTrue(actual instanceof HTTPQueryResponse);

        HTTPQueryResponse httpResponse = (HTTPQueryResponse) actual;
        assertEquals(Response.Status.NOT_FOUND, httpResponse.getHTTPStatus());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerReturningCollectionResponse() {
        // Given
        final SomeCollectionQuery query = new SomeCollectionQuery();

        // When
        final QueryResponse<SomeCollectionResponse> response = client().query(
                Contexts.empty(), query, SomeCollectionResponse.class);

        // Then
        assertTrue(response.isOK());
        assertEquals(1, response.getResult().getCount());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerThrowListOfErrors() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.setDoThrowSomeException(true);
        query.setAValue("some error message");
        query.setErrorCodes(Arrays.asList("a", "b"));

        // When
        final QueryResponse<SomeCollectionResponse> actual = client().query(
                Contexts.empty(), query, SomeCollectionResponse.class);
       
        // Then
        assertFalse(actual.isOK());
        assertEquals(query.getAValue(), actual.getReason().getCode());
        assertTrue(actual instanceof HTTPQueryResponse);

        HTTPQueryResponse httpResponse = (HTTPQueryResponse) actual;
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, httpResponse.getHTTPStatus());

        Collection<String> messages = actual.getReason().getMessages();
        final String[] actualMessages = messages.toArray(new String[messages.size()]);

        for (int i = 0; i < query.getErrorCodes().size(); i++) {
            assertEquals(query.getErrorCodes().get(i), actualMessages[i]);
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testAliasedQuery() throws MalformedURLException, URISyntaxException {
        // Given
        final String queryPath = NEED_VALIDATION_2_ALIAS;

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), queryPath).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPostQuery() throws MalformedURLException, URISyntaxException, JsonProcessingException {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = false;

        final ObjectWriter objectWriter = ObjectMapperProvider.INSTANCE.mapper().writer();
        final String requestEntity = objectWriter.writeValueAsString(query);

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), "some").toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, requestEntity);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testXKasperServerNameInHeader() throws MalformedURLException, URISyntaxException, UnknownHostException {
        // Given
        final String expectedServerName = InetAddress.getLocalHost().getCanonicalHostName();
        final NeedValidationWithAlias query = new NeedValidationWithAlias();

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), query.getClass().getSimpleName().replace("Query", "")).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedServerName, response.getHeaders().getFirst(HttpContextHeaders.HEADER_SERVER_NAME.toHeaderName()));
    }

    @Test
    public void testUnexposedCommandHandler() throws MalformedURLException, URISyntaxException {
        // Given
        final UnexposedQuery query = new UnexposedQuery();

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), query.getClass().getSimpleName().replace("Query", "")).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        // Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
