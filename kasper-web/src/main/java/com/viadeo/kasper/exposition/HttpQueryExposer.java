// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.context.impl.DefaultKasperId;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.query.QueryFactory;
import com.viadeo.kasper.query.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryParser;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.beans.Introspector;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;
import static javax.servlet.http.HttpServletResponse.*;

public class HttpQueryExposer extends HttpExposer {
    private static final long serialVersionUID = 8448984922303895624L;

    protected static final transient Logger QUERY_LOGGER = LoggerFactory.getLogger(HttpQueryExposer.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private static final Timer METRICLASSTIMER = METRICS.timer(name(HttpQueryExposer.class, "requests-time"));
    private static final Timer METRICLASSHANDLETIMER = METRICS.timer(name(HttpQueryExposer.class, "requests-handle-time"));
    private static final Meter METRICLASSREQUESTS = METRICS.meter(name(HttpQueryExposer.class, "requests"));
    private static final Meter METRICLASSERRORS = METRICS.meter(name(HttpQueryExposer.class, "errors"));

    // ------------------------------------------------------------------------

    private final static TypeReference<ImmutableSetMultimap<String, String>> mapOfStringsType = new TypeReference<ImmutableSetMultimap<String, String>>() {};

    interface QueryToQueryMap {
        SetMultimap<String, String> toQueryMap(final HttpServletRequest req, final HttpServletResponse resp) throws IOException;
    }

    private final static QueryToQueryMap jsonBodyToQueryMap = new QueryToQueryMap() {
        @Override
        public SetMultimap<String, String> toQueryMap(
                final HttpServletRequest req,
                final HttpServletResponse resp
        ) throws IOException {

            final ObjectMapper mapper = ObjectMapperProvider.INSTANCE.mapper();
            final JsonParser parser = mapper.reader().getFactory().createParser(req.getInputStream());

            final SetMultimap<String, String> queryMap = mapper.reader().readValue(parser, mapOfStringsType);

            return queryMap;
        }
    };

    private final static QueryToQueryMap queryStringToMap = new QueryToQueryMap() {
        @Override
        public SetMultimap<String, String> toQueryMap(
                final HttpServletRequest req,
                final HttpServletResponse resp
        ) throws IOException {

            final ImmutableSetMultimap.Builder<String, String> queryParams = new ImmutableSetMultimap.Builder<>();
            final Enumeration<String> keys = req.getParameterNames();

            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                queryParams.putAll(key, Arrays.asList(req.getParameterValues(key)));
            }

            return queryParams.build();
        }
    };

    // ------------------------------------------------------------------------

    private final Map<String, Class<? extends Query>> exposedQueries = Maps.newHashMap();
    private final transient QueryHandlersLocator queryHandlersLocator;
    private final transient QueryFactory queryAdapterFactory;
    private final ObjectMapper mapper;
    private final transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    public HttpQueryExposer(final QueryGateway queryGateway,
                            final QueryHandlersLocator queryHandlersLocator,
                            final QueryFactory queryAdapterFactory,
                            final ObjectMapper mapper) {

        this.queryGateway = queryGateway;
        this.queryHandlersLocator = queryHandlersLocator;
        this.queryAdapterFactory = queryAdapterFactory;
        this.mapper = mapper;
    }

    public HttpQueryExposer(final QueryGateway queryGateway, final QueryHandlersLocator queryLocator) {
        this(queryGateway, queryLocator, new QueryFactoryBuilder().create(), ObjectMapperProvider.INSTANCE.mapper());
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing queries ===============");

        /* expose all registered queries and commands */
        for (final QueryHandler queryHandler : queryHandlersLocator.getHandlers()) {
            expose(queryHandler);
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
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if ( ! req.getContentType().startsWith("application/json")) {
            sendError(SC_NOT_ACCEPTABLE, "Accepting only application/json; charset=utf-8", req, resp, null);
        } else {
            handleQuery(jsonBodyToQueryMap, req, resp);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleQuery(queryStringToMap, req, resp);
    }

    // ------------------------------------------------------------------------

    protected void handleQuery(final QueryToQueryMap queryMapper, final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
         /* Start request timer */
        final Timer.Context classTimer = METRICLASSTIMER.time();

        /* Create a kasper correlation id */
        final UUID requestCorrelationUUID = UUID.randomUUID();
        MDC.put("correlationId", requestCorrelationUUID.toString());
        resp.addHeader("UUID", requestCorrelationUUID.toString());

        /* Log starting request */
        QUERY_LOGGER.info("Processing HTTP Query '{}' '{}'", req.getMethod(), getFullRequestURI(req));

        /* always respond with a json stream (even if empty) */
        resp.setContentType(MediaType.APPLICATION_JSON + "; charset=utf-8");

        /*
         * lets be very defensive and catch every thing in order to not break
         * the contract with clients = JSON only
         */
        try {

            final String queryName = resourceName(req.getRequestURI());
            final Query query = parseQuery(queryMapper.toQueryMap(req, resp), queryName, req, resp);

            QueryResponse response = null;
            if (!resp.isCommitted()) {
                final Timer.Context queryHandleTimer = METRICS.timer(name(query.getClass(), "requests-handle-time")).time();
                final Timer.Context classHandleTimer = METRICLASSHANDLETIMER.time();

                response = handleQuery(queryName, query, req, resp, requestCorrelationUUID );

                queryHandleTimer.stop();
                classHandleTimer.stop();
            }

            /* need to check again as something might go wrong in handleQuery */
            if (!resp.isCommitted()) {
                sendResponse(queryName, response, req, resp);
            }

        } catch (final Throwable t) {
            sendError(
                    SC_INTERNAL_SERVER_ERROR,
                    String.format("Could not handle query [%s] with parameters [%s]", req.getRequestURI(), req.getQueryString()),
                    req, resp, t);

        } finally {
            /* Log & metrics */
            final long time = classTimer.stop();
            QUERY_LOGGER.info("Execution Time '{}' ms",time);
            METRICLASSREQUESTS.mark();
        }

        if (!resp.isCommitted()) {
            try {
                resp.flushBuffer();
            } catch (final IOException e) {
                LOGGER.warn("Error when trying to flush output buffer", e);
            }
        }
    }

    // we can not use send error as it will send text/html response.
    protected Query parseQuery(final SetMultimap<String, String> queryMap, final String queryName, final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        Query query = null;
        final Class<? extends Query> queryClass = exposedQueries.get(queryName);

        if (null == queryClass) {

            sendError(HttpServletResponse.SC_NOT_FOUND,
                      "No such query[" + queryName + "].",
                      req, resp, null);

        } else {

            final TypeAdapter<? extends Query> adapter = queryAdapterFactory.create(TypeToken.of(queryClass));

            try {

                query = adapter.adapt(new QueryParser(queryMap));

            } catch (final Throwable t) {
                sendError(SC_BAD_REQUEST, String.format(
                        "Unable to parse Query [%s] with parameters [%s]", queryName,
                        req.getQueryString()), req, resp, t);
            }
        }

        return query;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected QueryResponse handleQuery(final String queryName, final Query query, final HttpServletRequest req,
                                         final HttpServletResponse resp, final UUID requestCorrelationUUID)
            throws IOException {

        QueryResponse response = null;

         /* TODO: handle context from request */
        final Context context = new DefaultContextBuilder().build();
        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            ((AbstractContext) context).setKasperCorrelationId(new DefaultKasperId(requestCorrelationUUID));
        }

        try {

            response = queryGateway.retrieve(query, context);
            checkNotNull(response);

        } catch (final Throwable e) {
            /*
             * it is ok to eat all kind of exceptions as they occur at parsing
             * level so we know what approximately failed.
             */
            sendError(SC_INTERNAL_SERVER_ERROR,
                      String.format("ERROR Submiting query[%s] to Kasper platform.", queryName),
                      req, resp, e);
        }

        return response;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected void sendResponse(final String queryName, final QueryResponse response, final HttpServletRequest req,
                              final HttpServletResponse resp)
            throws IOException {

        final ObjectWriter writer = mapper.writer();

        final int status;
        if (response.isError()) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else {
            status = HttpServletResponse.SC_OK;
        }

        try {

            resp.setStatus(status);
            writer.writeValue(resp.getOutputStream(), response);

            /* Log the request */
            QUERY_LOGGER.info("HTTP Response {} '{}' : {}", req.getMethod(), req.getRequestURI(), status);

        } catch (final Throwable t) {
            sendError(SC_INTERNAL_SERVER_ERROR,
                      String.format("ERROR sending Response [%s] for query [%s]", response.getClass().getSimpleName(),queryName),
                      req, resp, t);
        } finally {
            try {
                resp.flushBuffer();
            } catch (final IOException e) {
                LOGGER.warn("Error when trying to flush output buffer", e);
            }
        }

    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    protected void sendError(final int status, final String message, final HttpServletRequest req,
                             final HttpServletResponse resp, final Throwable exception)
            throws IOException {

        if (null != exception) {
            LOGGER.error(message, exception);
        } else {
            LOGGER.error(message);
        }

        resp.setStatus(status, message);

        final ObjectWriter writer = mapper.writer();

        final KasperError error;
        if ((null != exception) && (null != exception.getMessage())) {
            error = new KasperError(CoreErrorCode.UNKNOWN_ERROR, message, exception.getMessage());
        } else {
            error = new KasperError(CoreErrorCode.UNKNOWN_ERROR, message);
        }

        writer.writeValue(resp.getOutputStream(), new QueryResponse<>(error));

        try {
            resp.flushBuffer();
        } catch (final IOException e) {
            LOGGER.warn("Error when trying to flush output buffer", e);
        }

        /* Log the request */
        QUERY_LOGGER.info("HTTP Response {} '{}' : {} {}", req.getMethod(), req.getRequestURI(), status, message, exception);

        /* Log error metric */
        METRICLASSERRORS.mark();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected HttpQueryExposer expose(final QueryHandler queryHandler) {
        checkNotNull(queryHandler);

        final TypeToken<? extends QueryHandler> typeToken = TypeToken.of(queryHandler.getClass());
        final Class<? super Query> queryClass = (Class<? super Query>) typeToken
                .getSupertype(QueryHandler.class)
                .resolveType(QueryHandler.class.getTypeParameters()[0])
                .getRawType();

        final String queryPath = queryToPath(queryClass);

        LOGGER.info("-> Exposing query[{}] at path[/{}]", queryClass.getSimpleName(),
                    getServletContext().getContextPath() + queryPath);

        putKey(queryPath, queryClass, exposedQueries);

        return this;
    }

    // ------------------------------------------------------------------------

    private String queryToPath(final Class<? super Query> exposedQuery) {
        return Introspector.decapitalize(exposedQuery.getSimpleName().replaceAll("Query", ""));
    }

}
