// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
<<<<<<< HEAD
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
=======
import java.beans.Introspector;
>>>>>>> 6e1ed4471229228230497e02473a807fafaed952

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.async.TypeListener;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.client.lib.ICallback;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.cqrs.command.impl.KasperErrorCommandResult;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.query.exposition.IQueryFactory;
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.KasperQueryAdapterException;
import com.viadeo.kasper.query.exposition.QueryBuilder;

/**
 * The Kasper java client
 */
<<<<<<< HEAD
public final class KasperClient {
	private static final KasperClient DEFAULT_KASPER_CLIENT = new KasperClientBuilder()
			.create();

	private final Client client;
	private final URL commandBaseLocation;
	private final URL queryBaseLocation;
	@VisibleForTesting
	final IQueryFactory queryFactory;

	// ------------------------------------------------------------------------

	public KasperClient() {
		this.client = DEFAULT_KASPER_CLIENT.client;
		this.commandBaseLocation = DEFAULT_KASPER_CLIENT.commandBaseLocation;
		this.queryBaseLocation = DEFAULT_KASPER_CLIENT.queryBaseLocation;
		this.queryFactory = DEFAULT_KASPER_CLIENT.queryFactory;
	}

	KasperClient(final IQueryFactory queryFactory, final ObjectMapper mapper,
			final URL commandBaseUrl, final URL queryBaseUrl) {
		final DefaultClientConfig cfg = new DefaultClientConfig();
		cfg.getSingletons().add(new JacksonJsonProvider(mapper));

		this.client = Client.create(cfg);
		this.commandBaseLocation = commandBaseUrl;
		this.queryBaseLocation = queryBaseUrl;
		this.queryFactory = queryFactory;
	}

	KasperClient(final IQueryFactory queryFactory, final Client client,
			final URL commandBaseUrl, final URL queryBaseUrl) {
		this.client = client;
		this.commandBaseLocation = commandBaseUrl;
		this.queryBaseLocation = queryBaseUrl;
		this.queryFactory = queryFactory;
	}

	// ------------------------------------------------------------------------
	// COMMANDS
	// ------------------------------------------------------------------------

	public ICommandResult send(final ICommand command) {
		checkNotNull(command);
		final ClientResponse response = client
				.resource(resolveCommandPath(command.getClass()))
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, command);
		return handleResponse(response);
	}

	// --

	public Future<? extends ICommandResult> sendAsync(final ICommand command) {
		checkNotNull(command);
		final Future<ClientResponse> futureResponse = client
				.asyncResource(resolveCommandPath(command.getClass()))
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, command);

		// we need to decorate the Future returned by jersey in order to handle
		// exceptions and populate according to it the command result
		return new Future<ICommandResult>() {
			public boolean cancel(final boolean mayInterruptIfRunning) {
				return futureResponse.cancel(mayInterruptIfRunning);
			}

			public boolean isCancelled() {
				return futureResponse.isCancelled();
			}

			public boolean isDone() {
				return futureResponse.isDone();
			}

			public ICommandResult get() throws InterruptedException,
					ExecutionException {
				return handleResponse(futureResponse.get());
			}

			public ICommandResult get(final long timeout, final TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return handleResponse(futureResponse.get(timeout, unit));
			}
		};
	}

	// --

	public void sendAsync(final ICommand command,
			final ICallback<ICommandResult> callback) {
		checkNotNull(command);
		client.asyncResource(resolveCommandPath(command.getClass()))
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.put(new TypeListener<ClientResponse>(ClientResponse.class) {
					@Override
					public void onComplete(final Future<ClientResponse> f)
							throws InterruptedException {
						try {
							callback.done(handleResponse(f.get()));
						} catch (final ExecutionException e) {
							throw new KasperClientException(e);
						}
					}
				}, command);
	}

	private ICommandResult handleResponse(final ClientResponse response) {
		final Status status = response.getClientResponseStatus();
		// handle errors
		if (status.getStatusCode() == 200) {
			return response.getEntity(KasperCommandResult.class);
		} else
			return response.getEntity(KasperErrorCommandResult.class);
	}

	// ------------------------------------------------------------------------
	// QUERIES
	// ------------------------------------------------------------------------

	public <T extends IQueryDTO> T query(final IQuery query,
			final Class<T> mapTo) {
		return query(query, TypeToken.of(mapTo));
	}

	public <T extends IQueryDTO> T query(final IQuery query,
			final TypeToken<T> mapTo) {
		checkNotNull(query);
		checkNotNull(mapTo);
		final ClientResponse response = client
				.resource(resolveQueryPath(query.getClass()))
				.queryParams(prepareQueryParams(query))
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		return handleQueryResponse(response, mapTo);
	}

	// --

	public <T extends IQueryDTO> Future<T> queryAsync(final IQuery query,
			final Class<T> mapTo) {
		return queryAsync(query, TypeToken.of(mapTo));
	}

	// FIXME should we also handle async in the platform side ?? Is it really
	// useful?
	public <T extends IQueryDTO> Future<T> queryAsync(final IQuery query,
			final TypeToken<T> mapTo) {
		checkNotNull(query);
		checkNotNull(mapTo);
		final Future<ClientResponse> futureResponse = client
				.asyncResource(resolveQueryPath(query.getClass()))
				.queryParams(prepareQueryParams(query))
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);

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

			public T get(final long timeout, final TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return handleQueryResponse(futureResponse.get(timeout, unit),
						mapTo);
			}
		};
	}

	// --

	public <T extends IQueryDTO> void queryAsync(final IQuery query,
			final Class<T> mapTo, final ICallback<T> callback) {
		queryAsync(query, TypeToken.of(mapTo), callback);
	}

	public <T extends IQueryDTO> void queryAsync(final IQuery query,
			final TypeToken<T> mapTo, final ICallback<T> callback) {
		checkNotNull(query);
		checkNotNull(mapTo);
		client.asyncResource(resolveQueryPath(query.getClass()))
				.queryParams(prepareQueryParams(query))
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.get(new TypeListener<ClientResponse>(ClientResponse.class) {
					@Override
					public void onComplete(final Future<ClientResponse> f)
							throws InterruptedException {
						try {
							callback.done(handleQueryResponse(f.get(), mapTo));
						} catch (final ExecutionException e) {
							throw new KasperClientException(e);
						}
					}
				});
	}

	private <T extends IQueryDTO> T handleQueryResponse(
			final ClientResponse response, final TypeToken<T> mapTo) {
		final Status status = response.getClientResponseStatus();
		// handle errors
		if (status.getStatusCode() == 200) {
			return response.getEntity(new GenericType<T>(mapTo.getType()));
		} else
			throw new KasperClientException("SERVER ERROR [status="
					+ status.getStatusCode() + ", reason="
					+ status.getReasonPhrase() + "]");
	}

	// --

	MultivaluedMap<String, String> prepareQueryParams(final IQuery query) {
		try {
			@SuppressWarnings("unchecked")
			final ITypeAdapter<IQuery> adapter = (ITypeAdapter<IQuery>) queryFactory
					.create(TypeToken.of(query.getClass()));

			final QueryBuilder queryBuilder = new QueryBuilder();
			adapter.adapt(query, queryBuilder);
			MultivaluedMap<String, String> map = new MultivaluedMapImpl();
			map.putAll(queryBuilder.build());
			return map;
		} catch (KasperQueryAdapterException ex) {
			throw new KasperClientException(ex);
		}
	}

	// ------------------------------------------------------------------------
	// RESOLVERS
	// ------------------------------------------------------------------------

	private URI resolveCommandPath(final Class<? extends ICommand> commandClass) {
		return resolvePath(commandBaseLocation, commandClass.getSimpleName()
				.replace("Command", ""), commandClass);
	}

	private URI resolveQueryPath(final Class<? extends IQuery> queryClass) {
		return resolvePath(queryBaseLocation, queryClass.getSimpleName()
				.replace("Query", ""), queryClass);
	}

	private URI resolvePath(final URL basePath, final String path,
			final Class<?> clazz) {
		try {
			return new URL(basePath, path).toURI();
		} catch (final MalformedURLException e) {
			throw cannotConstructURI(clazz, e);
		} catch (final URISyntaxException e) {
			throw cannotConstructURI(clazz, e);
		}
	}

	// ------------------------------------------------------------------------

	private KasperClientException cannotConstructURI(final Class<?> clazz,
			final Exception e) {
		return new KasperClientException(
				"Could not construct resource url for " + clazz, e);
	}
=======
public class KasperClient {
    private static final KasperClient DEFAULT_KASPER_CLIENT = new KasperClientBuilder().create();

    private final Client client;
    private final URL commandBaseLocation;
    private final URL queryBaseLocation;
    @VisibleForTesting
    final IQueryFactory queryFactory;

    // ------------------------------------------------------------------------
    
    public KasperClient() {
        this.client = DEFAULT_KASPER_CLIENT.client;
        this.commandBaseLocation = DEFAULT_KASPER_CLIENT.commandBaseLocation;
        this.queryBaseLocation = DEFAULT_KASPER_CLIENT.queryBaseLocation;
        this.queryFactory = DEFAULT_KASPER_CLIENT.queryFactory;
    }

    KasperClient(final IQueryFactory queryFactory, final ObjectMapper mapper, final URL commandBaseUrl, final URL queryBaseUrl) {
        final DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(mapper));
        
        this.client = Client.create(cfg);
        this.commandBaseLocation = commandBaseUrl;
        this.queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
    }

    KasperClient(final IQueryFactory queryFactory, final Client client, 
            final URL commandBaseUrl, final URL queryBaseUrl) {
        this.client = client;
        this.commandBaseLocation = commandBaseUrl;
        this.queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
    }

    // ------------------------------------------------------------------------
    // COMMANDS
    // ------------------------------------------------------------------------
    
    public ICommandResult send(final ICommand command) {
        checkNotNull(command);
        return client.resource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(KasperCommandResult.class, command);
    }

    // --
    
    public Future<? extends ICommandResult> sendAsync(final ICommand command) {
        checkNotNull(command);
        return client.asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(KasperCommandResult.class, command);
    }

    // --
    
    public void sendAsync(final ICommand command, final ICallback<ICommandResult> callback) {
        checkNotNull(command);
        client.asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(new TypeListener<KasperCommandResult>(KasperCommandResult.class) {
                    @Override
                    public void onComplete(final Future<KasperCommandResult> f) throws InterruptedException {
                        try {
                            callback.done(f.get());
                        } catch (final ExecutionException e) {
                            throw new KasperClientException(e);
                        }
                    }
                }, command);
    }

    // ------------------------------------------------------------------------
    // QUERIES
    // ------------------------------------------------------------------------
    
    public <T extends IQueryDTO> T query(final IQuery query, final Class<T> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);
        return client.resource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(mapTo);
    }

    // --
    
    // FIXME should we also handle async in the platform side ?? Is it really useful?
    public <T extends IQueryDTO> Future<T> queryAsync(final IQuery query, final Class<T> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);
        return client.asyncResource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(mapTo);
    }

    // --
    
    public <T extends IQueryDTO> void queryAsync(final IQuery query, final Class<T> mapTo, final ICallback<T> callback) {
        checkNotNull(query);
        checkNotNull(mapTo);
        client.asyncResource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(new TypeListener<T>(mapTo) {
                    @Override
                    public void onComplete(final Future<T> f) throws InterruptedException {
                        try {
                            callback.done(f.get());
                        } catch (final ExecutionException e) {
                            throw new KasperClientException(e);
                        }
                    }
                });
    }

    // --
    
    MultivaluedMap<String, String> prepareQueryParams(final IQuery query) {
        @SuppressWarnings("unchecked")
        final ITypeAdapter<IQuery> adapter = (ITypeAdapter<IQuery>) queryFactory.create(TypeToken.of(query.getClass()));

        final QueryBuilder queryBuilder = new QueryBuilder();
        adapter.adapt(query, queryBuilder);

        return queryBuilder.build();
    }

    // ------------------------------------------------------------------------
    // RESOLVERS
    // ------------------------------------------------------------------------
    
    private URI resolveCommandPath(final Class<? extends ICommand> commandClass) {
        return resolvePath(commandBaseLocation, Introspector.decapitalize(commandClass.getSimpleName().replaceFirst("Command", "")), commandClass);
    }

    private URI resolveQueryPath(final Class<? extends IQuery> queryClass) {
        return resolvePath(queryBaseLocation, Introspector.decapitalize(queryClass.getSimpleName().replaceFirst("Query", "")), queryClass);
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
    
    private KasperClientException cannotConstructURI(final Class<?> clazz, final Exception e) {
        return new KasperClientException("Could not construct resource url for " + clazz, e);
    }
>>>>>>> 6e1ed4471229228230497e02473a807fafaed952

}
