// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.async.TypeListener;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.http.HTTPCommandResponse;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.http.HTTPQueryResponse;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.exception.KasperQueryAdapterException;
import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryFactory;

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
 * <p>
 * <strong>Usage</strong><br />
 * 
 * KasperClient supports synchronous and asynchronous requests. Sending
 * asynchronous requests can be done by asking for a java Future or by passing a
 * {@link Callback callback} argument. For example
 * submitting a command asynchronously with a callback (we will use here a
 * client with its default configuration). <br/>
 * Command and query methods can throw KasperClientException, which are
 * unchecked exceptions in order to avoid boilerplate code.
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
 * Using a similar pattern you can submit a query.
 * </p>
 * <p>
 * <strong>Customization</strong><br />
 * 
 * To customize a KasperClient instance you can use the
 * {@link KasperClientBuilder}, implementing the builder pattern in order to
 * allow a fluent and intuitive construction of KasperClient instances.
 * </p>
 * <p>
 * <strong>Important notes</strong><br />
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
 * </p>
 */
public class KasperClient {
    private static final KasperClient DEFAULT_KASPER_CLIENT = new KasperClientBuilder().create();

    protected final Client client;
    protected final URL commandBaseLocation;
    protected final URL queryBaseLocation;

    private final Flags flags;

    @VisibleForTesting
    protected final QueryFactory queryFactory;

    @VisibleForTesting
    protected final HttpContextSerializer contextSerializer;

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

    /**
     * Creates a new KasperClient instance using the default
     * {@link KasperClientBuilder} configuration.
     */
    public KasperClient() {
        this.client = DEFAULT_KASPER_CLIENT.client;
        this.commandBaseLocation = DEFAULT_KASPER_CLIENT.commandBaseLocation;
        this.queryBaseLocation = DEFAULT_KASPER_CLIENT.queryBaseLocation;
        this.queryFactory = DEFAULT_KASPER_CLIENT.queryFactory;
        this.contextSerializer = DEFAULT_KASPER_CLIENT.contextSerializer;
        this.flags = Flags.defaults();
    }

    // --

    KasperClient(final QueryFactory queryFactory, final ObjectMapper mapper,
                 final URL commandBaseUrl, final URL queryBaseUrl,
                 final HttpContextSerializer contextSerializer,
                 final Flags flags) {

        final DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(mapper));

        this.client = Client.create(cfg);
        this.commandBaseLocation = commandBaseUrl;
        this.queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
        this.contextSerializer = contextSerializer;
        this.flags = flags;
    }

    KasperClient(final QueryFactory queryFactory, final ObjectMapper mapper,
                 final URL commandBaseUrl, final URL queryBaseUrl,
                 final HttpContextSerializer contextSerializer) {
        this(queryFactory, mapper, commandBaseUrl, queryBaseUrl, contextSerializer, Flags.defaults());
    }

    // --

    KasperClient(final QueryFactory queryFactory, final Client client,
                 final URL commandBaseUrl, final URL queryBaseUrl,
                 final HttpContextSerializer contextSerializer,
                 final Flags flags) {

        this.client = client;
        this.commandBaseLocation = commandBaseUrl;
        this.queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
        this.contextSerializer = contextSerializer;
        this.flags = flags;
    }

     KasperClient(final QueryFactory queryFactory, final Client client,
                  final URL commandBaseUrl, final URL queryBaseUrl,
                  final HttpContextSerializer contextSerializer) {
        this(queryFactory, client, commandBaseUrl, queryBaseUrl, contextSerializer, Flags.defaults());
     }

    // ------------------------------------------------------------------------
    // COMMANDS
    // ------------------------------------------------------------------------

    /**
     * Sends a command and waits until a response is returned.
     * 
     * @param command
     *            to submit
     * @return the command response, indicating if the command has been processed
     *         successfully or not (in that case you can get the error message
     *         from the command).
     * @throws KasperException
     *             KasperClientException if something went wrong.
     * @see CommandResponse
     */
    public CommandResponse send(final Context context, final Command command) {
        checkNotNull(command);

        final WebResource.Builder builder = client
                .resource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON);

        contextSerializer.serialize(context, builder);

        final ClientResponse response = builder.put(ClientResponse.class, command);

        return handleResponse(response);
    }

    // --

    /**
     * Sends a command and returns immediately a future allowing to retrieve the
     * response later.
     * 
     * @param command
     *            to submit
     * @return a Future allowing to retrieve the response later.
     * @throws KasperException
     *             if something went wrong.
     * @see CommandResponse
     */
    public Future<? extends CommandResponse> sendAsync(final Context context, final Command command) {
        checkNotNull(command);

        final AsyncWebResource.Builder builder = client
                .asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON);

        contextSerializer.serialize(context, builder);

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
     * @param command
     *            to submit
     * @param callback
     *            to call when the response is ready.
     * @throws KasperException
     *             if something went wrong.
     * @see CommandResponse
     */
    public void sendAsync(final Context context, final Command command, final Callback<CommandResponse> callback) {
        checkNotNull(command);

        final AsyncWebResource.Builder builder = client.asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON);

        contextSerializer.serialize(context, builder);

        builder.put(new TypeListener<ClientResponse>(ClientResponse.class) {
                    @Override
                    public void onComplete(final Future<ClientResponse> f)
                            throws InterruptedException {
                        try {
                            callback.done(handleResponse(f.get()));
                        } catch (final ExecutionException e) {
                            throw new KasperException(String.format("ERROR handling command [%s]",
                                    command.getClass()), e);
                        }
                    }
                }, command);
    }

    CommandResponse handleResponse(final ClientResponse clientResponse) {
        if (clientResponse.getType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {

            final CommandResponse response = clientResponse.getEntity(CommandResponse.class);

            /* Extract security token if it has been set */
            final MultivaluedMap<String, String> headers = clientResponse.getHeaders();
            if (headers.containsKey(HEADER_SECURITY_TOKEN)) {
                response.withSecurityToken(headers.getFirst(HEADER_SECURITY_TOKEN));
            }

            return new HTTPCommandResponse(Response.Status.fromStatusCode(clientResponse.getStatus()), response);

        } else {

            return new HTTPCommandResponse(
                    Response.Status.fromStatusCode(clientResponse.getStatus()),
                    CommandResponse.Status.ERROR,
                    new KasperReason(
                            CoreReasonCode.UNKNOWN_REASON,
                            "Response from platform uses an unsupported type: " + clientResponse.getType())
            );
        }
    }

    // ------------------------------------------------------------------------
    // QUERIES
    // ------------------------------------------------------------------------

    /**
     * Send a query and maps the result to a Response.
     * 
     * @param query
     *            to submit.
     * @param mapTo
     *            Response class to which we want to map the response.
     * @return an instance of the Response for this query.
     * @throws KasperException
     *             if something went wrong.
     */
    public <P extends QueryResult> QueryResponse<P> query(final Context context, final Query query, final Class<P> mapTo) {
        return query(context, query, TypeToken.of(mapTo));
    }

    /**
     * Send a query and maps the response to a Response. Here we use guavas
     * TypeToken allowing to define a generic type. This is useful if you want
     * to map the response to a IQueryCollectionResponse. <br/>
     * <p>
     * Type tokens are used like that:
     * 
     * <pre>
     * SomeCollectionResponse&lt;SomeResponse&gt; someResponseCollection = client.query(someQuery,
     *         new TypeToken&lt;SomeCollectionResponse&lt;SomeResponse&gt;&gt;());
     * </pre>
     * 
     * If you are not familiar with the concept of TypeTokens you can read <a
     * href="http://gafter.blogspot.fr/2006/12/super-type-tokens.html">this blog
     * post</a> who explains a bit more in details what it is about.
     * </p>
     * 
     * @param query
     *            to submit.
     * @param mapTo
     *            Response class to which we want to map the response.
     * @return an instance of the Response for this query.
     * @throws KasperException
     *             if something went wrong.
     */
    public <P extends QueryResult> QueryResponse<P> query(final Context context, final Query query, final TypeToken<P> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);

        final WebResource.Builder builder = client
                .resource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON);

        contextSerializer.serialize(context, builder);

        final ClientResponse response;
        if (flags.usePostForQueries()) {
            response = builder.post(ClientResponse.class, queryToSetMap(query));
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
     * @see KasperClient#query(Context, com.viadeo.kasper.cqrs.query.Query, Class)
     * @see KasperClient#sendAsync(Context, com.viadeo.kasper.cqrs.command.Command)
     */
    public <P extends QueryResult> Future<QueryResponse<P>> queryAsync(
            final Context context, final Query query, final TypeToken<P> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);

        final AsyncWebResource.Builder builder = client
                .asyncResource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON);

        contextSerializer.serialize(context, builder);

        final Future<ClientResponse> futureResponse;

        if (flags.usePostForQueries()) {
            futureResponse = builder.post(ClientResponse.class, queryToSetMap(query));
        } else {
            futureResponse = builder.get(ClientResponse.class);
        }

        return new QueryResponseFuture<P>(this, futureResponse, mapTo);
    }

    // --

    /**
     * @see KasperClient#query(Context, com.viadeo.kasper.cqrs.query.Query, Class)
     * @see KasperClient#sendAsync(Context, com.viadeo.kasper.cqrs.command.Command, Callback)
     */
    public <P extends QueryResult> void queryAsync(final Context context, final Query query, final Class<P> mapTo,
                                                   final Callback<QueryResponse<P>> callback) {
        queryAsync(context, query, TypeToken.of(mapTo), callback);
    }

    /**
     * @see KasperClient#query(Context, com.viadeo.kasper.cqrs.query.Query, Class)
     * @see KasperClient#sendAsync(Context, com.viadeo.kasper.cqrs.command.Command,
     *      Callback)
     */
    public <P extends QueryResult> void queryAsync(final Context context, final Query query, final TypeToken<P> mapTo,
                                                   final Callback<QueryResponse<P>> callback) {
        checkNotNull(query);
        checkNotNull(mapTo);

        final AsyncWebResource.Builder builder = client.asyncResource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON);

        contextSerializer.serialize(context, builder);

        final TypeListener<ClientResponse> typeListener = createTypeListener(query, mapTo, callback);

        if (flags.usePostForQueries()) {
            builder.post(typeListener, queryToSetMap(query));
        } else {
            builder.get(typeListener);
        }
    }

    private <P extends QueryResult> TypeListener<ClientResponse> createTypeListener(
            final Query query,
            final TypeToken<P> mapTo,
            final Callback<QueryResponse<P>> callback
    ) {
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

        if (clientResponse.getType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {

            final TypeToken mappedType = new TypeToken<QueryResponse<P>>() {
                    private static final long serialVersionUID = -6868146773459098496L;
                }.where(new TypeParameter<P>() { }, mapTo);

            final QueryResponse<P> response = clientResponse.getEntity(new GenericType<QueryResponse<P>>(mappedType.getType()));
            return new HTTPQueryResponse<P>(Response.Status.fromStatusCode(clientResponse.getStatus()), response);

        } else {

            return new HTTPQueryResponse<P>(
                    Response.Status.fromStatusCode(clientResponse.getStatus()),
                    new KasperReason(
                            CoreReasonCode.UNKNOWN_REASON,
                            "Response from platform uses an unsupported type: " + clientResponse.getType())
            );
        }
    }

    // --

    MultivaluedMap<String, String> prepareQueryParams(final Query query) {
            final MultivaluedMap<String, String> map = new MultivaluedMapImpl();

            if ( ! flags.usePostForQueries()) {
                for (final Map.Entry<String, String> entry : queryToSetMap(query).entries()) {
                    map.add(entry.getKey(), entry.getValue());
                }
            }

            return map;
    }

    private SetMultimap<String, String> queryToSetMap(final Query query) {
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
}
