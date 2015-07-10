// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.async.TypeListener;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.viadeo.kasper.api.domain.response.CoreReasonCode;
import com.viadeo.kasper.api.domain.response.KasperReason;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.api.domain.command.CommandResponse;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.api.domain.query.QueryResponse;
import com.viadeo.kasper.api.domain.query.QueryResult;
import com.viadeo.kasper.api.domain.event.Event;
import com.viadeo.kasper.api.domain.exception.KasperException;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.exception.KasperQueryAdapterException;
import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryFactory;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.beans.Introspector;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sun.jersey.api.client.ClientResponse.Status.ACCEPTED;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_ACCESS_TOKEN;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_SECURITY_TOKEN;

/**
 * <p>
 * KasperClient allows to submit commands and queries to a remote kasper
 * platform. It actually wraps all the logic of communication, errors and
 * resources location resolution.
 * </p>
 * <p>
 * Instances of <strong>KasperClient are thread safe and should be
 * reused</strong> as internally some caching is done in order to improve
 * performances (mainly to avoid java introspection overhead).
 * </p>
 *
 * <p>
 * <strong>Usage</strong>
 * </p>
 * <p>
 * KasperClient supports synchronous and asynchronous requests. Sending
 * asynchronous requests can be done by asking for a java Future or by passing a
 * {@link Callback callback} argument. For example
 * submitting a command asynchronously with a callback (we will use here a
 * client with its default configuration).
 * Command and query methods can throw KasperClientException, which are
 * unchecked exceptions in order to avoid boilerplate code.
 * </p>
 *
 * <pre>
 *      KasperClient client = new KasperClient();
 *
 *      client.sendAsync(someCommand, new ICallback&lt;ICommandResponse&gt;() {
 *          public void done(final ICommandResponse response) {
 *              // do something smart with my response
 *          }
 *      });
 *
 *      // or using a future
 *
 *      Future&lt;ICommandResponse&gt; futureCommandResponse = client.sendAsync(someCommand);
 *
 *      // do some other work while the command is being processed
 *      ...
 *
 *      // block until the response is obtained
 *      ICommandResponse commandResponse = futureCommandResponse.get();
 * </pre>
 *
 * <p>
 * Using a similar pattern you can submit a query.
 * </p>
 * <p>
 * <strong>Customization</strong>
 * </p>
 *
 * <p>
 * To customize a KasperClient instance you can use the
 * {@link KasperClientBuilder}, implementing the builder pattern in order to
 * allow a fluent and intuitive construction of KasperClient instances.
 * </p>
 *
 * <p>
 * <strong>Important notes</strong>
 * </p>
 *
 * <ul>
 * <li>Query implementations must be composed only of simple types (serialized
 * to litterals), if you need a complex query or some type used in your query is
 * not supported you should ask the team responsible of maintaining the kasper
 * platform to implement a custom
 * {@link com.viadeo.kasper.query.exposition.TypeAdapter} for that specific
 * type.</li>
 * <li>At the moment the Response to which the response should be mapped is free,
 * but take care it must match the responseing stream. This will probably change
 * in the future by making IQuery parameterized with a Response. Thus query
 * methods signature could change.</li>
 * </ul>
 */
public class KasperClient {
    private static final KasperClient DEFAULT_KASPER_CLIENT = new KasperClientBuilder().create();

    protected final Client client;
    protected final URL commandBaseLocation;
    protected final URL queryBaseLocation;
    protected final URL eventBaseLocation;

    private final Flags flags;

    @VisibleForTesting
    protected final QueryFactory queryFactory;

    @VisibleForTesting
    protected final HttpContextSerializer contextSerializer;

    private final String version;

    // ------------------------------------------------------------------------

    public static final class Flags {

        private boolean usePostForQueries = false;

        // -----

        public static Flags defaults() {
            return new Flags();
        }

        public Flags importFrom(final Flags flags) {
            this.usePostForQueries = flags.usePostForQueries();
            return this;
        }

        // -----

        public Flags usePostForQueries(final boolean flag) {
            this.usePostForQueries = flag;
            return this;
        }

        public boolean usePostForQueries() {
            return this.usePostForQueries;
        }

    }

    // ------------------------------------------------------------------------

    protected static String version() {
        final Optional<String> version = new ManifestReader(KasperClient.class).getKasperVersion();
        if (version.isPresent()) {
            return version.get();
        }
        return "nc";
    }

    // ------------------------------------------------------------------------

    /**
     * Creates a new KasperClient instance using the default
     * {@link KasperClientBuilder} configuration.
     */
    public KasperClient() {
        this(
            DEFAULT_KASPER_CLIENT.queryFactory,
            DEFAULT_KASPER_CLIENT.client,
            DEFAULT_KASPER_CLIENT.commandBaseLocation,
            DEFAULT_KASPER_CLIENT.queryBaseLocation,
            DEFAULT_KASPER_CLIENT.eventBaseLocation,
            DEFAULT_KASPER_CLIENT.contextSerializer,
            Flags.defaults()
        );
    }

    KasperClient(final QueryFactory queryFactory,
                 final Client client,
                 final URL commandBaseUrl,
                 final URL queryBaseUrl,
                 final URL eventBaseLocation,
                 final HttpContextSerializer contextSerializer,
                 final Flags flags) {

        this.client = checkNotNull(client);
        this.commandBaseLocation = checkNotNull(commandBaseUrl);
        this.queryBaseLocation = checkNotNull(queryBaseUrl);
        this.queryFactory = checkNotNull(queryFactory);
        this.eventBaseLocation = checkNotNull(eventBaseLocation);
        this.contextSerializer = checkNotNull(contextSerializer);
        this.flags = checkNotNull(flags);
        this.version = version();
    }

    KasperClient(final QueryFactory queryFactory,
                 final Client client,
                 final URL commandBaseUrl,
                 final URL queryBaseUrl,
                 final URL eventBaseLocation,
                 final HttpContextSerializer contextSerializer) {
        this(
            queryFactory,
            client,
            commandBaseUrl,
            queryBaseUrl,
            eventBaseLocation,
            contextSerializer,
            Flags.defaults()
        );
    }

    // ------------------------------------------------------------------------

    protected <BUILDER_OUT extends RequestBuilder<BUILDER_OUT>, BUILDER_IN extends RequestBuilder<BUILDER_OUT>> BUILDER_OUT configureBuilder(
            final Context context,
            final BUILDER_IN builder) {
        final BUILDER_OUT builderOut = builder
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpContextHeaders.HEADER_CLIENT_VERSION.toHeaderName(), version);

        contextSerializer.serialize(context, builderOut);

        return builderOut;
    }

    // ------------------------------------------------------------------------
    // COMMANDS
    // ------------------------------------------------------------------------

    /**
     * Sends a command and waits until a response is returned.
     *
     * @param context a related context
     * @param command to submit
     * @return the command response, indicating if the command has been processed
     *         successfully or not (in that case you can get the error message
     *         from the command).
     * @throws KasperException KasperClientException if something went wrong.
     * @see CommandResponse
     */
    public CommandResponse send(final Context context, final Command command) {
        checkNotNull(command);
        checkNotNull(context);

        final WebResource.Builder builder = configureBuilder(
            context,
            client.resource(resolveCommandPath(command.getClass()))
        );

        final ClientResponse response = builder.put(ClientResponse.class, command);

        return handleCommandResponse(response);
    }

    // --

    /**
     * Sends a command and returns immediately a future allowing to retrieve the
     * response later.
     *
     * @param context a related context
     * @param command to submit
     * @return a Future allowing to retrieve the response later.
     * @throws KasperException if something went wrong.
     * @see CommandResponse
     */
    public Future<? extends CommandResponse> sendAsync(final Context context, final Command command) {
        checkNotNull(command);
        checkNotNull(context);

        final AsyncWebResource.Builder builder = configureBuilder(
            context,
            client.asyncResource(resolveCommandPath(command.getClass()))
        );

        final Future<ClientResponse> futureResponse = builder.put(ClientResponse.class, command);

        // we need to decorate the Future returned by jersey in order to handle
        // exceptions and populate according to it the command response
        return new CommandResponseFuture(this, futureResponse);
    }

    // --

    /**
     * Sends a command and returns immediately, when the response is ready the
     * callback will be called with the obtained ICommandResponse as parameter.
     *
     * @param context a related context
     * @param command  to submit
     * @param callback to call when the response is ready.
     * @throws KasperException if something went wrong.
     * @see CommandResponse
     */
    public void sendAsync(final Context context, final Command command, final Callback<CommandResponse> callback) {
        checkNotNull(command);
        checkNotNull(context);

        final AsyncWebResource.Builder builder = configureBuilder(
            context,
            client.asyncResource(resolveCommandPath(command.getClass()))
        );

        builder.put(new TypeListener<ClientResponse>(ClientResponse.class) {
            @Override
            public void onComplete(final Future<ClientResponse> f)
                    throws InterruptedException {
                try {

                    callback.done(handleCommandResponse(f.get()));

                } catch (final ExecutionException e) {
                    throw new KasperException(String.format(
                        "ERROR handling command [%s]",
                        command.getClass()), e
                    );
                }
            }
        }, command);
    }

    /**
     * Interpret platform's response to a sent command
     *
     * @param clientResponse the native response
     * @return the CommandResponse deserialized object
     */
    CommandResponse handleCommandResponse(final ClientResponse clientResponse) {
        if (checkNotNull(clientResponse).getType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {

            final CommandResponse response = clientResponse.getEntity(CommandResponse.class);

            /* Extract security token if it has been set in headers */
            final MultivaluedMap<String, String> headers = clientResponse.getHeaders();

            if (headers.containsKey(HEADER_SECURITY_TOKEN.toHeaderName())) {
                response.withSecurityToken(headers.getFirst(HEADER_SECURITY_TOKEN.toHeaderName()));
            }
            if (headers.containsKey(HEADER_ACCESS_TOKEN.toHeaderName())) {
                response.withAccessToken(headers.getFirst(HEADER_ACCESS_TOKEN.toHeaderName()));
            }

            return new HTTPCommandResponse(
                safeStatusFromCode(clientResponse.getStatus()),
                response
            );

        } else {

            return new HTTPCommandResponse(
                safeStatusFromCode(clientResponse.getStatus()),
                CommandResponse.Status.ERROR,
                new KasperReason(
                    CoreReasonCode.UNKNOWN_REASON,
                    "Response from platform uses an unsupported type: "
                        + clientResponse.getType()
                )
            );
        }
    }

    // ------------------------------------------------------------------------
    // EVENTS
    // ------------------------------------------------------------------------

    /**
     * Sends an event and waits until a response is returned.
     *
     * @param context a related context
     * @param event to submit
     * @throws KasperException if something went wrong.
     */
    public void emit(final Context context, final Event event) {
        checkNotNull(event);
        checkNotNull(context);

        final WebResource.Builder builder = configureBuilder(
            context,
            client.resource(resolveEventPath(event.getClass()))
        );

        try {

            final ClientResponse response = builder.put(ClientResponse.class, event);
            final ClientResponse.Status status = response.getClientResponseStatus();

            if ( ! ACCEPTED.equals(status)) {
                throw new KasperException("event submission failed with status <" + status.getReasonPhrase() + ">");
            }

        } catch (final Exception e) {
            throw new KasperException("Unable to send event : " + event.getClass().getName(), e);
        }

    }

    // ------------------------------------------------------------------------
    // QUERIES
    // ------------------------------------------------------------------------

    /**
     * Send a query and maps the result to a Response.
     *
     * @param context a related context
     * @param query to submit.
     * @param <P> the type of result.
     * @param mapTo Response class to which we want to map the response.
     * @return an instance of the Response for this query.
     * @throws KasperException if something went wrong.
     */
    public <P extends QueryResult> QueryResponse<P> query(
            final Context context, final Query query, final Class<P> mapTo) {
        return query(context, query, TypeToken.of(mapTo));
    }

    /**
     * Send a query and maps the response to a Response. Here we use guavas
     * TypeToken allowing to define a generic type. This is useful if you want
     * to map the response to a IQueryCollectionResponse.
     * <p>
     * Type tokens are used like that:
     *
     * <pre>
     * SomeCollectionResponse&lt;SomeResponse&gt; someResponseCollection = client.query(someQuery,
     *         new TypeToken&lt;SomeCollectionResponse&lt;SomeResponse&gt;&gt;());
     * </pre>
     *
     * <p>
     * If you are not familiar with the concept of TypeTokens you can read <a
     * href="http://gafter.blogspot.fr/2006/12/super-type-tokens.html">this blog
     * post</a> who explains a bit more in details what it is about.
     *
     *
     * @param context a related context
     * @param query to submit.
     * @param mapTo Response class to which we want to map the response.
     * @param <P> the type of the <code>QueryResult</code>
     * @return an instance of the Response for this query.
     * @throws KasperException if something went wrong.
     */
    public <P extends QueryResult> QueryResponse<P> query(
            final Context context, final Query query, final TypeToken<P> mapTo)
    {
        checkNotNull(query);
        checkNotNull(mapTo);
        checkNotNull(context);

        WebResource webResource = client.resource(resolveQueryPath(query.getClass()));
        if ( ! flags.usePostForQueries()) {
            webResource = webResource.queryParams(prepareQueryParams(query));
        }

        final WebResource.Builder builder = configureBuilder(context, webResource);

        final ClientResponse response;
        if (flags.usePostForQueries()) {
            response = builder.post(ClientResponse.class, queryToJson(query));
        } else {
            response = builder.get(ClientResponse.class);
        }

        return handleQueryResponse(response, mapTo);
    }

    // --

    public <P extends QueryResult> Future<QueryResponse<P>> queryAsync(
            final Context context, final Query query, final Class<P> mapTo) {
        return queryAsync(context, query, TypeToken.of(mapTo));
    }

    /**
     * FIXME should we also handle async in the platform side ?? Is it really
     * useful?
     *
     * @param context a related context
     * @param query to submit.
     * @param mapTo Response class to which we want to map the response.
     * @param <P> the type of the <code>QueryResult</code>
     * @return a future of the Response for this query.
     *
     * @see KasperClient#query(Context, com.viadeo.kasper.api.domain.query.Query, Class)
     * @see KasperClient#sendAsync(Context, com.viadeo.kasper.api.domain.command.Command)
     */
    public <P extends QueryResult> Future<QueryResponse<P>> queryAsync(
            final Context context, final Query query, final TypeToken<P> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);
        checkNotNull(context);

        AsyncWebResource asyncWebResource = client.asyncResource(resolveQueryPath(query.getClass()));
        if ( ! flags.usePostForQueries()) {
            asyncWebResource = asyncWebResource.queryParams(prepareQueryParams(query));
        }

        final AsyncWebResource.Builder builder = configureBuilder(context, asyncWebResource);

        final Future<ClientResponse> futureResponse;

        if (flags.usePostForQueries()) {
            futureResponse = builder.post(ClientResponse.class, queryToJson(query));
        } else {
            futureResponse = builder.get(ClientResponse.class);
        }

        return new QueryResponseFuture<P>(this, futureResponse, mapTo);
    }

    // --

    /**
     * @param context a related context
     * @param query to submit.
     * @param mapTo Response class to which we want to map the response.
     * @param callback a callback
     * @param <P> the type of the <code>QueryResult</code>
     *
     * @see KasperClient#query(Context, com.viadeo.kasper.api.domain.query.Query, Class)
     * @see KasperClient#sendAsync(Context, com.viadeo.kasper.api.domain.command.Command, Callback)
     */
    public <P extends QueryResult> void queryAsync(
            final Context context,
            final Query query,
            final Class<P> mapTo,
            final Callback<QueryResponse<P>> callback) {
        queryAsync(context, query, TypeToken.of(mapTo), callback);
    }

    /**
     * @param context a related context
     * @param query to submit.
     * @param mapTo TypeToken to which we want to map the response.
     * @param callback a callback
     * @param <P> the type of the <code>QueryResult</code>
     *
     * @see KasperClient#query(Context, com.viadeo.kasper.api.domain.query.Query, Class)
     * @see KasperClient#sendAsync(Context, com.viadeo.kasper.api.domain.command.Command,
     *      Callback)
     */
    public <P extends QueryResult> void queryAsync(
            final Context context,
            final Query query,
            final TypeToken<P> mapTo,
            final Callback<QueryResponse<P>> callback) {
        checkNotNull(query);
        checkNotNull(mapTo);
        checkNotNull(context);
        checkNotNull(callback);

        AsyncWebResource asyncWebResource = client.asyncResource(resolveQueryPath(query.getClass()));
        if ( ! flags.usePostForQueries()) {
            asyncWebResource = asyncWebResource.queryParams(prepareQueryParams(query));
        }

        final AsyncWebResource.Builder builder = configureBuilder(context, asyncWebResource);

        final TypeListener<ClientResponse> typeListener = createTypeListener(query, mapTo, callback);

        if (flags.usePostForQueries()) {
            builder.post(typeListener, query);
        } else {
            builder.get(typeListener);
        }
    }

    private <P extends QueryResult> TypeListener<ClientResponse> createTypeListener(
            final Query query,
            final TypeToken<P> mapTo,
            final Callback<QueryResponse<P>> callback) {
        return new TypeListener<ClientResponse>(ClientResponse.class) {
            @Override
            public void onComplete(final Future<ClientResponse> f) throws InterruptedException {
                try {

                    callback.done(handleQueryResponse(f.get(), mapTo));

                } catch (final ExecutionException e) {
                    throw new KasperException("ERROR handling query[" + query.getClass() + "]", e);
                }
            }
        };
    }

    <P extends QueryResult> QueryResponse<P> handleQueryResponse(final ClientResponse clientResponse,
                                                                 final TypeToken<P> mapTo) {

        if (checkNotNull(clientResponse).getType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {

            final TypeToken mappedType = new TypeToken<QueryResponse<P>>() { }
                                            .where(
                                                new TypeParameter<P>() { },
                                                checkNotNull(mapTo)
                                            );

            final QueryResponse<P> response = clientResponse.getEntity(
                    new GenericType<QueryResponse<P>>(mappedType.getType())
            );

            return new HTTPQueryResponse<P>(
                    safeStatusFromCode(clientResponse.getStatus()),
                    response
            );

        } else {

            return new HTTPQueryResponse<P>(
                safeStatusFromCode(clientResponse.getStatus()),
                new KasperReason(
                    CoreReasonCode.UNKNOWN_REASON,
                    "Response from platform uses an unsupported type: " + clientResponse.getType()
                )
            );

        }
    }

    // --

    MultivaluedMap<String, String> prepareQueryParams(final Query query) {
        checkNotNull(query);

        final MultivaluedMap<String, String> map = new MultivaluedMapImpl();

        if ( ! flags.usePostForQueries()) {
            for (final Map.Entry<String, String> entry : queryToSetMap(query).entries()) {
                map.add(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    private String queryToJson(final Query query) {
        final ObjectWriter objectWriter = ObjectMapperProvider.INSTANCE.objectWriter();
        final String queryToJson;

        try {
            queryToJson = objectWriter.writeValueAsString(query);
        } catch (final JsonProcessingException e) {
            throw new KasperException(String.format("ERROR generating query string for [%s]", query.getClass()), e);
        }

        return queryToJson;
    }

    private SetMultimap<String, String> queryToSetMap(final Query query) {
        checkNotNull(query);

        @SuppressWarnings("unchecked")
        final TypeAdapter<Query> adapter = (TypeAdapter<Query>)
                queryFactory.create(TypeToken.of(query.getClass()));

        final QueryBuilder queryBuilder = new QueryBuilder();
        try {

            adapter.adapt(query, queryBuilder);

        } catch (final KasperQueryAdapterException ex) {

            throw new KasperException(String.format(
                "ERROR generating query string for [%s]",
                query.getClass()
            ), ex);

        } catch (final Exception ex) {

            throw new KasperException(String.format(
                "ERROR generating query string for [%s]",
                query.getClass()
            ), ex);

        }

        return queryBuilder.build();
    }

    // ------------------------------------------------------------------------
    // RESOLVERS
    // ------------------------------------------------------------------------

    protected URI resolveCommandPath(final Class<? extends Command> commandClass) {
        final String className = commandClass.getSimpleName().replace("Command", "");
        return resolvePath(commandBaseLocation, Introspector.decapitalize(className), commandClass);
    }

    protected URI resolveQueryPath(final Class<? extends Query> queryClass) {
        final String className = queryClass.getSimpleName().replace("Query", "");
        return resolvePath(queryBaseLocation, Introspector.decapitalize(className), queryClass);
    }

    protected URI resolveEventPath(final Class<? extends Event> eventClass) {
        final String className = eventClass.getSimpleName().replace("Event", "");
        return resolvePath(eventBaseLocation, Introspector.decapitalize(className), eventClass);
    }

    private URI resolvePath(final URL basePath, final String path, final Class clazz) {
        try {

            return new URL(basePath, path).toURI();

        } catch (final MalformedURLException e) {
            throw cannotConstructURI(clazz, e);
        } catch (final URISyntaxException e) {
            throw cannotConstructURI(clazz, e);
        }
    }

    // ------------------------------------------------------------------------

    private KasperException cannotConstructURI(final Class clazz, final Exception e) {
        return new KasperException("Could not construct resource url for " + clazz, e);
    }

    private Response.Status safeStatusFromCode(final int code) {
        final Response.Status status = Response.Status.fromStatusCode(code);
        if (null == status) {
            return Response.Status.INTERNAL_SERVER_ERROR;
        } else {
            return status;
        }
    }

}
