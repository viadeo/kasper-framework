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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.context.impl.DefaultKasperId;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.QueryService;
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
import java.beans.Introspector;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class HttpQueryExposer extends HttpExposer {
    private static final long serialVersionUID = 8448984922303895624L;
    protected static final transient Logger QUERY_LOGGER = LoggerFactory.getLogger(HttpQueryExposer.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private static final Timer METRICLASSTIMER = METRICS.timer(name(HttpQueryExposer.class, "requests-time"));
    private static final Histogram METRICLASSREQUESTSTIME = METRICS.histogram(name(HttpQueryExposer.class, "requests-times"));
    private static final Meter METRICLASSREQUESTS = METRICS.meter(name(HttpQueryExposer.class, "requests"));
    private static final Meter METRICLASSERRORS = METRICS.meter(name(HttpQueryExposer.class, "errors"));

    private final Map<String, Class<? extends Query>> exposedQueries = Maps.newHashMap();
    private final transient QueryServicesLocator queryServicesLocator;
    private final transient QueryFactory queryAdapterFactory;
    private final ObjectMapper mapper;
    private final transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    public HttpQueryExposer(final QueryGateway queryGateway, final QueryServicesLocator queryLocator) {
        this(queryGateway, queryLocator, new QueryFactoryBuilder().create(), ObjectMapperProvider.INSTANCE.mapper());
    }

    public HttpQueryExposer(final QueryGateway queryGateway,
                            final QueryServicesLocator queryServicesLocator,
                            final QueryFactory queryAdapterFactory, final ObjectMapper mapper) {

        this.queryGateway = queryGateway;
        this.queryServicesLocator = queryServicesLocator;
        this.queryAdapterFactory = queryAdapterFactory;
        this.mapper = mapper;
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("\n=============== Exposing queries ===============");

        /* expose all registered queries and commands */
        for (final QueryService<? extends Query, ?> queryService : queryServicesLocator.getServices()) {
            expose(queryService);
        }

        if (exposedQueries.isEmpty()) {
            LOGGER.warn("No Query has been exposed.");
        } else {
            LOGGER.info("Total exposed " + exposedQueries.size() + " queries.");
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    // again can not use sendError
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        /* Start request timer */
        final Timer.Context classTimer = METRICLASSTIMER.time();

        /* Create a request correlation id */
        final UUID requestCorrelationUUID = UUID.randomUUID();
        MDC.put("requestCorrelationId", requestCorrelationUUID.toString());
        resp.addHeader("UUID", requestCorrelationUUID.toString());

        /* Log starting request */
        QUERY_LOGGER.info("Processing HTTP Query [{}]: {} {}", requestCorrelationUUID, req.getMethod(), getFullRequestURI(req));
        long startTime = System.currentTimeMillis();

        // TODO we should think of providing some more information to client in
        // case of failure. We also need to be sure that those infos a really
        // useful

        /* always respond with a json stream (even if empty) */
        resp.setContentType("application/json; charset=utf-8");

        /*
         * lets be very defensive and catch every thing in order to not break
         * the contract with clients = JSON only
         */
        try {
            final String queryName = resourceName(req.getRequestURI());
            final Query query = parseQuery(queryName, req, resp, requestCorrelationUUID, startTime);

            QueryResult<?> result = null;
            if (!resp.isCommitted()) {
                result = handleQuery(queryName, query, resp, requestCorrelationUUID, startTime);
            }

            /* need to check again as something might go wrong in handleQuery */
            if (!resp.isCommitted()) {
                sendResult(queryName, result, resp, requestCorrelationUUID, startTime);
            }

        } catch (final Throwable t) {
            sendError(
                    SC_INTERNAL_SERVER_ERROR,
                    String.format("Could not handle query [%s] with parameters [%s]",
                            req.getRequestURI(), req.getQueryString()), resp, t,
                            requestCorrelationUUID, startTime);

        } finally {
            /* Log metrics */
            final long time = classTimer.stop();
            METRICLASSREQUESTSTIME.update(time);
            METRICLASSREQUESTS.mark();
        }

        resp.flushBuffer();
    }

    // ------------------------------------------------------------------------

    // we can not use send error as it will send text/html response.
    protected Query parseQuery(final String queryName, final HttpServletRequest req, final HttpServletResponse resp,
                               final UUID requestCorrelationUUID, final long startTime)
            throws IOException {

        Query query = null;
        final Class<? extends Query> queryClass = exposedQueries.get(queryName);

        if (null == queryClass) {

            sendError(HttpServletResponse.SC_NOT_FOUND,
                      "No such query[" + queryName + "].",
                      resp, null, requestCorrelationUUID, startTime);

        } else {

            final TypeAdapter<? extends Query> adapter = queryAdapterFactory.create(TypeToken.of(queryClass));

            final ImmutableSetMultimap.Builder<String, String> queryParams = new ImmutableSetMultimap.Builder<>();

            final Enumeration<String> keys = req.getParameterNames();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                queryParams.putAll(key, Arrays.asList(req.getParameterValues(key)));
            }

            try {

                query = adapter.adapt(new QueryParser(queryParams.build()));

            } catch (final Throwable t) {
                /*
                 * OK lets catch any exception that could occur during
                 * deserialization and try to send back
                 */
                sendError(SC_BAD_REQUEST, String.format(
                        "Unable to parse Query [%s] with parameters [%s]", queryName,
                        req.getQueryString()), resp, t, requestCorrelationUUID, startTime);
            }
        }

        return query;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected QueryResult<?> handleQuery(final String queryName, final Query query, final HttpServletResponse resp,
                                         final UUID requestCorrelationUUID, final long startTime)
            throws IOException {

        QueryResult<?> result = null;

        try {

            /*
             * checking if the response has not been sent, if it is true this
             * means that an error happened and has been handled
             *
             * FIXME: handle context from request
             *
             */
            final Context context = new DefaultContextBuilder().build();
            if (AbstractContext.class.isAssignableFrom(context.getClass())) {
                ((AbstractContext) context).setKasperCorrelationId(new DefaultKasperId(requestCorrelationUUID));
            }

            result = queryGateway.retrieve(query, new DefaultContextBuilder().build());

        } catch (final Throwable e) {
            /*
             * it is ok to eat all kind of exceptions as they occur at parsing
             * level so we know what approximatively failed.
             */
            sendError(SC_INTERNAL_SERVER_ERROR,
                      String.format("ERROR Submiting query[%s] to Kasper platform.", queryName),
                      resp, e, requestCorrelationUUID, startTime);
        }

        return result;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected void sendResult(final String queryName, final QueryResult<?> result, final HttpServletResponse resp,
                              final UUID requestCorrelationUUID, final long startTime)
            throws IOException {

        final ObjectWriter writer = mapper.writer();

        final int status;
        if (result.isError()) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else {
            status = HttpServletResponse.SC_OK;
        }

        try {

            resp.setStatus(status);
            writer.writeValue(resp.getOutputStream(), result);

            /* Log the request */
            QUERY_LOGGER.info("HTTP Response [{}]: '{}' Execution Time '{}' ms ",
                              requestCorrelationUUID, status, System.currentTimeMillis() - startTime);

        } catch (final Throwable t) {
            sendError(SC_INTERNAL_SERVER_ERROR,
                      String.format("ERROR sending Result [%s] for query [%s]", result.getClass().getSimpleName(),queryName),
                      resp, t, requestCorrelationUUID, startTime);
        }

    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    protected void sendError(final int status, final String message, final HttpServletResponse resp, final Throwable exception,
                             final UUID requestCorrelationUUID, final long startTime)
            throws IOException {

        if (exception != null) {
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

        writer.writeValue(resp.getOutputStream(), new QueryResult<>(error));

        resp.flushBuffer();

        /* Log the request */
        QUERY_LOGGER.info("HTTP Response [{}]: '{}' Execution Time '{}' ms ",
                          requestCorrelationUUID, status, System.currentTimeMillis() - startTime);

        /* Log error metric */
        METRICLASSERRORS.mark();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    HttpQueryExposer expose(final QueryService<? extends Query, ?> queryService) {
        checkNotNull(queryService);

        final TypeToken<? extends QueryService> typeToken = TypeToken.of(queryService.getClass());
        final Class<? super Query> queryClass = (Class<? super Query>) typeToken
                .getSupertype(QueryService.class)
                .resolveType(QueryService.class.getTypeParameters()[0])
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
