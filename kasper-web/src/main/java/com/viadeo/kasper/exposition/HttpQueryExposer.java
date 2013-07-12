// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.query.exposition.QueryFactory;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.QueryParser;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class HttpQueryExposer extends HttpExposer {
    private static final long serialVersionUID = 8448984922303895624L;

    private final Map<String, Class<? extends Query>> exposedQueries = Maps.newHashMap();
    private final QueryServicesLocator queryServicesLocator;
    private final QueryFactory queryAdapterFactory;
    private final ObjectMapper mapper;
    
    // ------------------------------------------------------------------------

    public HttpQueryExposer(final Platform platform, final QueryServicesLocator queryLocator) {
        this(platform, queryLocator, new QueryFactoryBuilder().create(), ObjectMapperProvider.instance.mapper());
    }

    public HttpQueryExposer(final Platform platform, final QueryServicesLocator queryServicesLocator,
            final QueryFactory queryAdapterFactory, final ObjectMapper mapper) {
        super(platform);
        this.queryServicesLocator = queryServicesLocator;
        this.queryAdapterFactory = queryAdapterFactory;
        this.mapper = mapper;
    }

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing queries ===============");
        // expose all registered queries and commands
        for (final QueryService<? extends Query, ? extends QueryDTO> queryService : queryServicesLocator
                .getServices()) {
            expose(queryService);
        }
        if (exposedQueries.isEmpty())
            LOGGER.warn("No Query has been exposed.");
        else
            LOGGER.info("Total exposed " + exposedQueries.size() + " queries.");
        LOGGER.info("=================================================");
    }

    // ------------------------------------------------------------------------

    // again can not use sendError
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {

        // TODO we should think of providing some more information to client in
        // case of failure. We also need to be sure that those infos a really
        // useful

        // always respond with a json stream (even if empty)
        resp.setContentType("application/json; charset=utf-8");

        /*
         * lets be very defensive and catch every thing in order to not break
         * the contract with clients = JSON only
         */
        try {
            final String queryName = resourceName(req.getRequestURI());
            final Query query = parseQuery(queryName, req, resp);

            QueryDTO dto = null;
            if (!resp.isCommitted()) {
                dto = handleQuery(queryName, query, resp);
            }

            // need to check again as something might go wrong in handleQuery
            if (!resp.isCommitted()) {
                sendDTO(queryName, dto, resp);
            }

        } catch (final Throwable t) {
            sendError(SC_INTERNAL_SERVER_ERROR, "Could not handle query[" + req.getRequestURI() + "] and parameters["
                    + req.getQueryString() + "]", resp, t);

        }

        resp.flushBuffer();
    }

    // ------------------------------------------------------------------------

    // we can not use send error as it will send text/html response.
    protected Query parseQuery(final String queryName, final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        Query query = null;
        final Class<? extends Query> queryClass = exposedQueries.get(queryName);

        if (queryClass == null) {

            sendError(HttpServletResponse.SC_NOT_FOUND, "No such query[" + queryName + "].", resp, null);

        } else {

            final TypeAdapter<? extends Query> adapter = queryAdapterFactory.create(TypeToken.of(queryClass));
            final Map<String, List<String>> queryParams = new HashMap<>();

            final Enumeration<String> keys = req.getParameterNames();
            while (keys.hasMoreElements()) {
                final String key = (String) keys.nextElement();
                queryParams.put(key, Arrays.asList(req.getParameterValues(key)));
            }

            try {

                query = adapter.adapt(new QueryParser(queryParams));

            } catch (final Throwable t) {
                /*
                 * OK lets catch any exception that could occur during
                 * deserialization and try to send back
                 */
                sendError(SC_BAD_REQUEST,
                        "Unable to parse Query[" + queryName + "] and parameters [" + req.getQueryString() + "].",
                        resp, t);
            }
        }

        return query;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected QueryDTO handleQuery(final String queryName, final Query query, final HttpServletResponse resp)
            throws IOException {

        QueryDTO dto = null;

        try {

            /*
             * checking if the response has not been sent, if it is true this
             * means that an error happened and has been handled
             */
            dto = platform().getQueryGateway().retrieve(query, new DefaultContextBuilder().build());

        } catch (final Throwable e) {
            /*
             * it is ok to eat all kind of exceptions as they occur at parsing
             * level so we know what approximatively failed.
             */
            sendError(SC_INTERNAL_SERVER_ERROR, "ERROR Submiting query[" + queryName + "] to Kasper platform.", resp, e);
        }

        return dto;
    }

    // ------------------------------------------------------------------------

    // can not use sendError it is forcing response to text/html
    protected void sendDTO(final String queryName, final QueryDTO dto, final HttpServletResponse resp)
            throws IOException {

        final ObjectWriter writer = mapper.writer();

        try {

            writer.writeValue(resp.getOutputStream(), dto);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (final Throwable t) {
            sendError(SC_INTERNAL_SERVER_ERROR, "ERROR sending DTO[" + dto.getClass().getSimpleName() + "] for query["
                    + queryName + "].", resp, t);
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    protected void sendError(final int status, final String message, final HttpServletResponse resp,
            final Throwable exception) throws IOException {

        if (exception != null) {
            LOGGER.error(message, exception);
        } else {
            LOGGER.error(message);
        }

        resp.setStatus(status, message);

        final ObjectWriter writer = mapper.writer();

        final KasperQueryException queryException;
        if (exception instanceof KasperQueryException)
            queryException = (KasperQueryException) exception;
        // FIXME I am not sure if we should get the most precise cause here by descending recursively in the stack 
        // trace or just send the message
        else {
            if (exception != null) {
                queryException = KasperQueryException.exception(message).reason(exception).create();
            } else
                queryException = KasperQueryException.exception(message).create();

            queryException.fillInStackTrace();
        }

        writer.writeValue(resp.getOutputStream(), queryException);

        resp.flushBuffer();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    HttpQueryExposer expose(final QueryService<? extends Query, ? extends QueryDTO> queryService) {
        checkNotNull(queryService);

        final TypeToken<? extends QueryService> typeToken = TypeToken.of(queryService.getClass());
        final Class<? super Query> queryClass = (Class<? super Query>) typeToken.getSupertype(QueryService.class)
                .resolveType(QueryService.class.getTypeParameters()[0]).getRawType();

        final String queryPath = queryToPath(queryClass);
        LOGGER.info("Exposing query[{}] at path[/{}]", queryClass.getSimpleName(), getServletContext().getContextPath()
                + queryPath);
        putKey(queryPath, queryClass, exposedQueries);

        return this;
    }

    // ------------------------------------------------------------------------

    private String queryToPath(final Class<? super Query> exposedQuery) {
        return Introspector.decapitalize(exposedQuery.getSimpleName().replaceAll("Query", ""));
    }

}
