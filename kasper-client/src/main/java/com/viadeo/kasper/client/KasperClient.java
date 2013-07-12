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
import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.async.TypeListener;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.viadeo.kasper.client.lib.Callback;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.query.exposition.QueryFactory;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.KasperQueryAdapterException;
import com.viadeo.kasper.query.exposition.QueryBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.beans.Introspector;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>
 * KasperClient allows to submit commands and queries to a remote kasper platform. It actually wraps all the logic of
 * communication, errors and resources location resolution.
 * </p>
 * <p>
 * Instances of <strong>KasperClient are thread safe and should be reused</strong> as internally some caching is done in
 * order to improve performances (mainly to avoid java introspection overhead).
 * </p>
 * <p>
 * <strong>Usage</strong><br />
 * KasperClient supports synchronous and asynchronous requests. Sending asynchronous requests can be done by asking for
 * a java Future or by passing a {@link com.viadeo.kasper.client.lib.Callback callback} argument. For example submitting a command asynchronously
 * with a callback (we will use here a client with its default configuration). <br/>
 * Command and query methods can throw KasperClientException, which are unchecked exceptions in order to avoid
 * boilerplate code.
 * 
 * <pre>
 *      KasperClient client = new KasperClient();
 *      
 *      client.sendAsync(someCommand, new ICallback&lt;ICommandResult&gt;() {
 *          public void done(ICommandResult result) {
 *              // do something smart with my result
 *          }
 *      });
 *      
 *      // or using a future
 *      
 *      Future&lt;ICommandResult&gt; futureCommandResult = client.sendAsync(someCommand);
 *      
 *      // do some other work while the command is being processed
 *      ...
 *      
 *      // block until the result is obtained
 *      ICommandResult commandResult = futureCommandResult.get();
 * </pre>
 * 
 * Using a similar pattern you can submit a query.
 * </p>
 * <p>
 * <strong>Customization</strong><br />
 * To customize a KasperClient instance you can use the {@link KasperClientBuilder}, implementing the builder pattern in
 * order to allow a fluent and intuitive construction of KasperClient instances.
 * </p>
 * <p>
 * <strong>Important notes</strong><br />
 * <ul>
 * <li>Query implementations must be composed only of simple types (serialized to litterals), if you need a complex
 * query or some type used in your query is not supported you should ask the team responsible of maintaining the kasper
 * platform to implement a custom {@link com.viadeo.kasper.query.exposition.TypeAdapter} for that specific type.</li>
 * <li>At the moment the DTO to which the result should be mapped is free, but take care it must match the resulting
 * stream. This will probably change in the future by making IQuery parameterized with a DTO. Thus query methods
 * signature could change.</li>
 * </ul>
 * </p>
 */
public class KasperClient {
    private static final KasperClient DEFAULT_KASPER_CLIENT = new KasperClientBuilder().create();

    private final Client client;
    private final URL commandBaseLocation;
    private final URL queryBaseLocation;

    @VisibleForTesting
    final QueryFactory queryFactory;

    // ------------------------------------------------------------------------

    /**
     * Creates a new KasperClient instance using the default {@link KasperClientBuilder} configuration.
     */
    public KasperClient() {
        this.client = DEFAULT_KASPER_CLIENT.client;
        this.commandBaseLocation = DEFAULT_KASPER_CLIENT.commandBaseLocation;
        this.queryBaseLocation = DEFAULT_KASPER_CLIENT.queryBaseLocation;
        this.queryFactory = DEFAULT_KASPER_CLIENT.queryFactory;
    }

    // --

    KasperClient(final QueryFactory queryFactory, final ObjectMapper mapper, final URL commandBaseUrl,
            final URL queryBaseUrl) {

        final DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(mapper));

        this.client = Client.create(cfg);
        this.commandBaseLocation = commandBaseUrl;
        this.queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
    }

    // --

    KasperClient(final QueryFactory queryFactory, final Client client, final URL commandBaseUrl, final URL queryBaseUrl) {

        this.client = client;
        this.commandBaseLocation = commandBaseUrl;
        this.queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
    }

    // ------------------------------------------------------------------------
    // COMMANDS
    // ------------------------------------------------------------------------

    /**
     * Sends a command and waits until a result is returned.
     * 
     * @param command to submit
     * @return the command result, indicating if the command has been processed successfully or not (in that case you
     * can get the error message from the command).
     * @throws KasperException KasperClientException if something went wrong.
     * @see CommandResult
     */
    public CommandResult send(final Command command) {
        checkNotNull(command);

        final ClientResponse response = client.resource(resolveCommandPath(command.getClass()))
                                        .accept(MediaType.APPLICATION_JSON)
                                        .type(MediaType.APPLICATION_JSON)
                                        .put(ClientResponse.class, command);

        return handleResponse(response);
    }

    // --

    /**
     * Sends a command and returns immediately a future allowing to retrieve the result later.
     * 
     * @param command to submit
     * @return a Future allowing to retrieve the result later.
     * @throws KasperException if something went wrong.
     * @see CommandResult
     */
    public Future<? extends CommandResult> sendAsync(final Command command) {
        checkNotNull(command);

        final Future<ClientResponse> futureResponse = client.asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).put(ClientResponse.class, command);

        // we need to decorate the Future returned by jersey in order to handle
        // exceptions and populate according to it the command result
        return new Future<CommandResult>() {
            public boolean cancel(final boolean mayInterruptIfRunning) {
                return futureResponse.cancel(mayInterruptIfRunning);
            }

            public boolean isCancelled() {
                return futureResponse.isCancelled();
            }

            public boolean isDone() {
                return futureResponse.isDone();
            }

            public CommandResult get() throws InterruptedException, ExecutionException {
                return handleResponse(futureResponse.get());
            }

            public CommandResult get(final long timeout, final TimeUnit unit) throws InterruptedException,
                    ExecutionException, TimeoutException {
                return handleResponse(futureResponse.get(timeout, unit));
            }
        };
    }

    // --

    /**
     * Sends a command and returns immediately, when the response is ready the callback will be called with the obtained
     * ICommandResult as parameter.
     * 
     * @param command to submit
     * @param callback to call when the response is ready.
     * @throws KasperException if something went wrong.
     * @see CommandResult
     */
    public void sendAsync(final Command command, final Callback<CommandResult> callback) {
        checkNotNull(command);

        client.asyncResource(resolveCommandPath(command.getClass()))
                            .accept(MediaType.APPLICATION_JSON)
                            .type(MediaType.APPLICATION_JSON)
                            .put(new TypeListener<ClientResponse>(ClientResponse.class) {
                                @Override
                                public void onComplete(final Future<ClientResponse> f) throws InterruptedException {
                                    try {
                                        callback.done(handleResponse(f.get()));
                                    } catch (final ExecutionException e) {
                                        throw new KasperException("ERROR handling command [" + command.getClass() + "]", e);
                                    }
                                }
                            }, command);
    }

    private CommandResult handleResponse(final ClientResponse response) {
        // handle errors
        return response.getEntity(CommandResult.class);
    }

    // ------------------------------------------------------------------------
    // QUERIES
    // ------------------------------------------------------------------------

    /**
     * Send a query and maps the result to a DTO.
     * 
     * @param query to submit.
     * @param mapTo DTO class to which we want to map the result.
     * @return an instance of the DTO for this query.
     * @throws KasperException if something went wrong.
     */
    public <T extends QueryDTO> T query(final Query query, final Class<T> mapTo) {
        return query(query, TypeToken.of(mapTo));
    }

    /**
     * Send a query and maps the result to a DTO. Here we use guavas TypeToken allowing to define a generic type. This
     * is useful if you want to map the result to a IQueryCollectionDTO. <br/>
     * <p>
     * Type tokens are used like that:
     * 
     * <pre>
     * SomeCollectionDTO&lt;SomeDTO&gt; someDTOCollection = client.query(someQuery, new TypeToken&lt;SomeCollectionDTO&lt;SomeDTO&gt;&gt;());
     * </pre>
     * 
     * If you are not familiar with the concept of TypeTokens you can read <a
     * href="http://gafter.blogspot.fr/2006/12/super-type-tokens.html">this blog post</a> who explains a bit more in
     * details what it is about.
     * </p>
     * 
     * @param query to submit.
     * @param mapTo DTO class to which we want to map the result.
     * @return an instance of the DTO for this query.
     * @throws KasperException if something went wrong.
     */
    public <T extends QueryDTO> T query(final Query query, final TypeToken<T> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);

        final ClientResponse response = client.resource(resolveQueryPath(query.getClass()))
                                              .queryParams(prepareQueryParams(query))
                                              .accept(MediaType.APPLICATION_JSON)
                                              .type(MediaType.APPLICATION_JSON)
                                              .get(ClientResponse.class);

        return handleQueryResponse(response, mapTo);
    }

    // --

    public <T extends QueryDTO> Future<T> queryAsync(final Query query, final Class<T> mapTo) {
        return queryAsync(query, TypeToken.of(mapTo));
    }

    /**
     * FIXME should we also handle async in the platform side ?? Is it really useful?
     * 
     * @see KasperClient#query(com.viadeo.kasper.cqrs.query.Query, Class)
     * @see KasperClient#sendAsync(com.viadeo.kasper.cqrs.command.Command)
     */
    public <T extends QueryDTO> Future<T> queryAsync(final Query query, final TypeToken<T> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);

        final Future<ClientResponse> futureResponse = client.asyncResource(resolveQueryPath(query.getClass()))
                                                            .queryParams(prepareQueryParams(query))
                                                            .accept(MediaType.APPLICATION_JSON)
                                                            .type(MediaType.APPLICATION_JSON)
                                                            .get(ClientResponse.class);

        return new Future<T>() {
            public boolean cancel(final boolean mayInterruptIfRunning) {
                return futureResponse.cancel(mayInterruptIfRunning);
            }

            public boolean isCancelled() {
                return futureResponse.isCancelled();
            }

            public boolean isDone() {
                return futureResponse.isDone();
            }

            public T get() throws InterruptedException, ExecutionException {
                return handleQueryResponse(futureResponse.get(), mapTo);
            }

            public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return handleQueryResponse(futureResponse.get(timeout, unit), mapTo);
            }
        };
    }

    // --

    /**
     * @see KasperClient#query(com.viadeo.kasper.cqrs.query.Query, Class)
     * @see KasperClient#sendAsync(com.viadeo.kasper.cqrs.command.Command, com.viadeo.kasper.client.lib.Callback)
     */
    public <T extends QueryDTO> void queryAsync(final Query query, final Class<T> mapTo, final Callback<T> callback) {

        queryAsync(query, TypeToken.of(mapTo), callback);
    }

    /**
     * @see KasperClient#query(com.viadeo.kasper.cqrs.query.Query, Class)
     * @see KasperClient#sendAsync(com.viadeo.kasper.cqrs.command.Command, com.viadeo.kasper.client.lib.Callback)
     */
    public <T extends QueryDTO> void queryAsync(final Query query, final TypeToken<T> mapTo,
            final Callback<T> callback) {
        checkNotNull(query);
        checkNotNull(mapTo);

        client.asyncResource(resolveQueryPath(query.getClass())).queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(new TypeListener<ClientResponse>(ClientResponse.class) {
                    @Override
                    public void onComplete(final Future<ClientResponse> f) throws InterruptedException {
                        try {

                            callback.done(handleQueryResponse(f.get(), mapTo));

                        } catch (final ExecutionException e) {
                            throw new KasperException("ERROR handling query[" + query.getClass() + "]", e);
                        }
                    }
                });
    }

    private <T extends QueryDTO> T handleQueryResponse(final ClientResponse response, final TypeToken<T> mapTo) {
        final Status status = response.getClientResponseStatus();
        
        // handle errors
        if (status.getStatusCode() == 200) {
            return response.getEntity(new GenericType<T>(mapTo.getType()));
        } else {
            final KasperQueryException exception = response.getEntity(KasperQueryException.class);
            // TODO: need to clean it, otherwise it will contain garbage stack trace from jackson deserialization
            exception.fillInStackTrace();
            throw exception;
        }
    }

    // --

    MultivaluedMap<String, String> prepareQueryParams(final Query query) {
        try {
            @SuppressWarnings("unchecked")
            final TypeAdapter<Query> adapter = (TypeAdapter<Query>) queryFactory.create(TypeToken.of(query.getClass()));

            final QueryBuilder queryBuilder = new QueryBuilder();
            adapter.adapt(query, queryBuilder);

            final MultivaluedMap<String, String> map = new MultivaluedMapImpl();
            map.putAll(queryBuilder.build());

            return map;

        } catch (final KasperQueryAdapterException ex) {
            throw new KasperException("ERROR generating query string for [" + query.getClass() + "]", ex);
        } catch (final Exception ex) {
            throw new KasperException("ERROR generating query string for [" + query.getClass() + "]", ex);
        }
    }

    // ------------------------------------------------------------------------
    // RESOLVERS
    // ------------------------------------------------------------------------

    private URI resolveCommandPath(final Class<? extends Command> commandClass) {
        final String className = commandClass.getSimpleName().replace("Command", "");
        return resolvePath(commandBaseLocation, Introspector.decapitalize(className), commandClass);
    }

    private URI resolveQueryPath(final Class<? extends Query> queryClass) {
        final String className = queryClass.getSimpleName().replace("Query", "");
        return resolvePath(queryBaseLocation, Introspector.decapitalize(className), queryClass);
    }

    private URI resolvePath(final URL basePath, final String path, final Class<?> clazz) {
        try {

            return new URL(basePath, path).toURI();

        } catch (final MalformedURLException e) {
            throw cannotConstructURI(clazz, e);
        } catch (final URISyntaxException e) {
            throw cannotConstructURI(clazz, e);
        }
    }

    // ------------------------------------------------------------------------

    private KasperException cannotConstructURI(final Class<?> clazz, final Exception e) {
        return new KasperException("Could not construct resource url for " + clazz, e);
    }

}
