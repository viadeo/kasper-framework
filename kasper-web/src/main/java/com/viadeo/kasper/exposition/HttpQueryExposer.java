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
import static javax.servlet.http.HttpServletResponse.*;

public class HttpQueryExposer extends HttpExposer {
    private static final long serialVersionUID = 8448984922303895624L;
    protected static final transient Logger QUERY_LOGGER = LoggerFactory.getLogger(HttpQueryExposer.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private static final Timer METRICLASSTIMER = METRICS.timer(name(HttpQueryExposer.class, "requests-time"));
    private static final Histogram METRICLASSREQUESTSTIME = METRICS.histogram(name(HttpQueryExposer.class, "requests-times"));
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
    private final transient QueryServicesLocator queryServicesLocator;
    private final transient QueryFactory queryAdapterFactory;
    private final ObjectMapper mapper;
    private final transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    public HttpQueryExposer(final QueryGateway queryGateway,
                            final QueryServicesLocator queryServicesLocator,
                            final QueryFactory queryAdapterFactory, final ObjectMapper mapper) {

        this.queryGateway = queryGateway;
        this.queryServicesLocator = queryServicesLocator;
        this.queryAdapterFactory = queryAdapterFactory;
        this.mapper = mapper;
    }

    public HttpQueryExposer(final QueryGateway queryGateway, final QueryServicesLocator queryLocator) {
        this(queryGateway, queryLocator, new QueryFactoryBuilder().create(), ObjectMapperProvider.INSTANCE.mapper());
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing queries ===============");

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

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if ( ! req.getContentType().startsWith("application/json")) {
            sendError(SC_NOT_ACCEPTABLE, "Accepting only application/json; charset=utf-8", req, resp, null);
        } else {
            handleQuery(jsonBodyToQueryMap, req, resp);
        }
    }

    // again can not use sendError
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleQuery(queryStringToMap, req, resp);
    }

    // ------------------------------------------------------------------------

    protected void handleQuery(final QueryToQueryMap queryMapper, final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
         /* Start request timer */
        final Timer.Context classTimer = METRICLASSTIMER.time();

        /* Create a request correlation id */
        final UUID requestCorrelationUUID = UUID.randomUUID();
        MDC.put("correlationId", requestCorrelationUUID.toString());
        resp.addHeader("UUID", requestCorrelationUUID.toString());

        /* Log starting request */
        QUERY_LOGGER.info("Processing HTTP Query '{}' '{}'", req.getMethod(), getFullRequestURI(req));

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
            final Query query = parseQuery(queryMapper.toQueryMap(req, resp), queryName, req, resp);

            QueryResult<?> result = null;
            if (!resp.isCommitted()) {
                result = handleQuery(queryName, query, req, resp, requestCorrelationUUID );
            }

            /* need to check again as something might go wrong in handleQuery */
            if (!resp.isCommitted()) {
                sendResult(queryName, result, req, resp);
            }
        } catch (final Throwable t) {
            sendError(
                    SC_INTERNAL_SERVER_ERROR,
                    String.format("Could not handle query [%s] with parameters [%s]", req.getRequestURI(), req.getQueryString()),
                    req, resp, t);

        } finally {
            /* Log metrics */
            final long time = classTimer.stop();
            QUERY_LOGGER.info("Execution Time '{}' ms",time);
            METRICLASSREQUESTSTIME.update(time);
            METRICLASSREQUESTS.mark();
        }

        resp.flushBuffer();
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
                /*
                 * OK lets catch any exception that could occur during
                 * deserialization and try to send back
                 */
                sendError(SC_BAD_REQUEST, String.format(
                        "Unable to parse Query [%s] with parameters [%s]", queryName,
                        req.getQueryString()), req, resp, t);
            }
        }

        return query;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected QueryResult<?> handleQuery(final String queryName, final Query query, final HttpServletRequest req,
                                         final HttpServletResponse resp, final UUID requestCorrelationUUID)
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

            result = queryGateway.retrieve(query, context);
            checkNotNull(result);

        } catch (final Throwable e) {
            /*
             * it is ok to eat all kind of exceptions as they occur at parsing
             * level so we know what approximately failed.
             */
            sendError(SC_INTERNAL_SERVER_ERROR,
                      String.format("ERROR Submiting query[%s] to Kasper platform.", queryName),
                      req, resp, e);
        }

        return result;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected void sendResult(final String queryName, final QueryResult<?> result, final HttpServletRequest req,
                              final HttpServletResponse resp)
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
            QUERY_LOGGER.info("HTTP Response {} '{}' : {}", req.getMethod(), req.getRequestURI(), status);

        } catch (final Throwable t) {
            sendError(SC_INTERNAL_SERVER_ERROR,
                      String.format("ERROR sending Result [%s] for query [%s]", result.getClass().getSimpleName(),queryName),
                      req, resp, t);
        } finally {
            resp.getWriter().flush();
        }

    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    protected void sendError(final int status, final String message, final HttpServletRequest req,
                             final HttpServletResponse resp, final Throwable exception)
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
        QUERY_LOGGER.info("HTTP Response {} '{}' : {} {}", req.getMethod(), req.getRequestURI(), status, message, exception);

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
