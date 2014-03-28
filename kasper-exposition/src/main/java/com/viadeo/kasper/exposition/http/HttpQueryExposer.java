// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import com.viadeo.kasper.query.exposition.query.QueryFactory;
import com.viadeo.kasper.query.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpQueryExposer extends HttpExposer<Query, QueryResponse> {
    private static final long serialVersionUID = 8448984922303895624L;

    private final Map<String, Class<? extends Query>> exposedQueries = Maps.newHashMap();
    private final transient List<ExposureDescriptor<Query,QueryHandler>> descriptors;
    private final transient QueryGateway queryGateway;

    private final ObjectToHttpServletResponse objectToHttpResponse;
    private final HttpServletRequestToObject httpRequestToObjectWithJson;
    private final HttpServletRequestToObject httpRequestToObjectWithString;

    // ------------------------------------------------------------------------

    public HttpQueryExposer(final Platform platform, final List<ExposureDescriptor<Query,QueryHandler>> descriptors) {
        this(
                platform.getQueryGateway(),
                platform.getMeta(),
                descriptors,
                new QueryFactoryBuilder().create(),
                new HttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }

    public HttpQueryExposer(final QueryGateway queryGateway,
                            final Meta meta,
                            final List<ExposureDescriptor<Query,QueryHandler>> descriptors,
                            final QueryFactory queryAdapterFactory,
                            final HttpContextDeserializer contextDeserializer,
                            final ObjectMapper mapper) {
        super(contextDeserializer, meta);
        this.queryGateway = checkNotNull(queryGateway);
        this.descriptors = checkNotNull(descriptors);

        this.objectToHttpResponse = new ObjectToHttpServletResponse(mapper);
        this.httpRequestToObjectWithJson = new HttpServletRequestToObject.JsonToObjectMapper(mapper);
        this.httpRequestToObjectWithString = new HttpServletRequestToObject.StringRequestToObjectMapper(mapper, queryAdapterFactory);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing queries ===============");

        /* expose all registered queries and commands */
        for (final ExposureDescriptor<Query,QueryHandler> descriptor : descriptors) {
            expose(descriptor);
        }

        if (exposedQueries.isEmpty()) {
            LOGGER.warn("No Query has been exposed.");
        } else {
            LOGGER.info("Total exposed " + exposedQueries.size() + " queries.");
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObjectWithJson, objectToHttpResponse, req, resp);
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObjectWithString, objectToHttpResponse, req, resp);
    }

    protected void checkMediaType(final HttpServletRequest httpRequest) throws HttpExposerException {
        if( "POST".equals(httpRequest.getMethod()) ){
            if( null == httpRequest.getContentType() || ! httpRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE) ){
                throw new HttpExposerException(
                        CoreReasonCode.UNSUPPORTED_MEDIA_TYPE,
                        "Accepting and producing only " + MediaType.APPLICATION_JSON_VALUE
                );
            }
        }
    }

    @Override
    protected QueryResponse createErrorResponse(CoreReasonCode code, List<String> reasons) {
        return QueryResponse.error(new KasperReason(code, reasons));
    }

    @Override
    protected boolean isManageable(final String requestName) {
       return exposedQueries.containsKey(checkNotNull(requestName));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends Query> getInputClass(final String inputName) {
        return exposedQueries.get(checkNotNull(inputName));
    }

    @Override
    public QueryResponse doHandle(Query query, Context context) throws Exception {
        return queryGateway.retrieve(query, context);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected HttpQueryExposer expose(final ExposureDescriptor<Query,QueryHandler> descriptor) {
        checkNotNull(descriptor);

        final TypeToken<? extends QueryHandler> typeToken = TypeToken.of(descriptor.getHandler());
        final Class<? super Query> queryClass = (Class<? super Query>) typeToken
                .getSupertype(QueryHandler.class)
                .resolveType(QueryHandler.class.getTypeParameters()[0])
                .getRawType();

        final String queryPath = queryToPath(queryClass);
        final List<String> aliases = AliasRegistry.aliasesFrom(queryClass);
        final String queryName = queryClass.getSimpleName();

        LOGGER.info("-> Exposing query[{}] at path[/{}]", queryName,
                    getServletContext().getContextPath() + queryPath);

        for (final String alias : aliases) {
            LOGGER.info("-> Exposing query[{}] at path[/{}]",
                    queryName,
                    getServletContext().getContextPath() + alias);
        }

        putKey(queryPath, queryClass, exposedQueries);

        getAliasRegistry().register(queryPath, aliases);

        return this;
    }

    // ------------------------------------------------------------------------

    private String queryToPath(final Class<? super Query> exposedQuery) {
        return Introspector.decapitalize(exposedQuery.getSimpleName().replaceAll("Query", ""));
    }

}
