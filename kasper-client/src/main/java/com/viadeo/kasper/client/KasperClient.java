/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.joda.time.DateTime;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.async.TypeListener;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.ICommandResult.Status;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public final class KasperClient {
    private final static KasperClient _defaultKasperClient = new Builder().create();

    private final Client client;
    private final URL commandBaseLocation;
    private final URL queryBaseLocation;
    private final QueryFactory queryFactory;

    public KasperClient() {
        client = _defaultKasperClient.client;
        commandBaseLocation = _defaultKasperClient.commandBaseLocation;
        queryBaseLocation = _defaultKasperClient.queryBaseLocation;
        queryFactory = _defaultKasperClient.queryFactory;
    }

    private KasperClient(final QueryFactory queryFactory, final ObjectMapper mapper, final URL commandBaseUrl, final URL queryBaseUrl) {
        DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(mapper));
        client = Client.create(cfg);
        commandBaseLocation = commandBaseUrl;
        queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
    }

    private KasperClient(final QueryFactory queryFactory, Client client, final URL commandBaseUrl, final URL queryBaseUrl) {
        this.client = client;
        commandBaseLocation = commandBaseUrl;
        queryBaseLocation = queryBaseUrl;
        this.queryFactory = queryFactory;
    }

    public ICommandResult send(final ICommand command) {
        checkNotNull(command);
        return client.resource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(KasperCommandResult.class, command);
    }

    public Future<? extends ICommandResult> sendAsync(final ICommand command) {
        checkNotNull(command);
        return client.asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(KasperCommandResult.class, command);
    }

    public void sendAsync(final ICommand command, final Callback<ICommandResult> callback) {
        checkNotNull(command);
        client.asyncResource(resolveCommandPath(command.getClass()))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(new TypeListener<KasperCommandResult>(KasperCommandResult.class) {
                    @Override
                    public void onComplete(Future<KasperCommandResult> f) throws InterruptedException {
                        try {
                            callback.done(f.get());
                        }
                        catch (ExecutionException e) {
                            throw new KasperClientException(e);
                        }
                    }
                }, command);
    }

    public <T extends IQueryDTO> T query(final IQuery query, final Class<T> mapTo) {
        checkNotNull(query);
        checkNotNull(mapTo);
        return client.resource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(mapTo);
    }

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

    public <T extends IQueryDTO> void queryAsync(final IQuery query, final Class<T> mapTo, final Callback<T> callback) {
        checkNotNull(query);
        checkNotNull(mapTo);

        client.asyncResource(resolveQueryPath(query.getClass()))
                .queryParams(prepareQueryParams(query))
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .get(new TypeListener<T>(mapTo) {
                    @Override
                    public void onComplete(Future<T> f) throws InterruptedException {
                        try {
                            callback.done(f.get());
                        }
                        catch (ExecutionException e) {
                            throw new KasperClientException(e);
                        }
                    }
                });
    }

    MultivaluedMap<String, String> prepareQueryParams(IQuery query) {
        @SuppressWarnings("unchecked")
        TypeAdapter<IQuery> adapter = (TypeAdapter<IQuery>) queryFactory.create(TypeToken.typeFor(query.getClass()));

        QueryBuilder queryBuilder = new QueryBuilder();
        adapter.adapt(query, queryBuilder);

        return queryBuilder.build();
    }

    private URI resolveCommandPath(Class<? extends ICommand> commandClass) {
        return resolvePath(commandBaseLocation, commandClass.getSimpleName().replaceFirst("Command", ""), commandClass);
    }

    private URI resolveQueryPath(Class<? extends IQuery> queryClass) {
        return resolvePath(queryBaseLocation, queryClass.getSimpleName().replaceFirst("Query", ""), queryClass);
    }

    private URI resolvePath(URL basePath, String path, Class<?> clazz) {
        try {
            return new URL(basePath, path).toURI();
        }
        catch (MalformedURLException e) {
            throw _canNotConstructURI(clazz, e);
        }
        catch (URISyntaxException e) {
            throw _canNotConstructURI(clazz, e);
        }
    }

    private KasperClientException _canNotConstructURI(Class<?> clazz, Exception e) {
        return new KasperClientException("Could not construct resource url for " + clazz, e);
    }

    public static class Builder {
        private Client client;
        private ObjectMapper mapper;
        private URL commandBaseLocation;
        private URL queryBaseLocation;
        private QueryFactory queryFactory;
        private Map<Type, TypeAdapter<?>> adapters = new HashMap<Type, TypeAdapter<?>>();
        private List<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();

        public Builder use(ObjectMapper mapper) {
            checkNotNull(mapper);
            this.mapper = mapper;
            return this;
        }

        public Builder use(QueryFactory queryFactory) {
            checkNotNull(queryFactory);
            this.queryFactory = queryFactory;
            return this;
        }

        public Builder use(TypeAdapter<?> adapter) {
            checkNotNull(adapter);
            adapters.put(ReflectionGenericsResolver.getParameterTypeFromClass(adapter.getClass(), TypeAdapter.class, 0).get(), adapter);
            return this;
        }

        public Builder use(TypeAdapterFactory factory) {
            checkNotNull(factory);
            factories.add(factory);
            return this;
        }

        public Builder queryBaseLocation(URL url) {
            checkNotNull(url);
            queryBaseLocation = url;
            return this;
        }

        public Builder commandBaseLocation(URL url) {
            checkNotNull(url);
            commandBaseLocation = url;
            return this;
        }

        // maybe make it public?
        Builder client(Client client) {
            this.client = checkNotNull(client);
            return this;
        }

        public KasperClient create() {
            if (mapper == null) {
                mapper = defaultMapper();
            }
            if (commandBaseLocation == null) {
                commandBaseLocation = createURL("http://kasper-platform/kasper/command");
            }
            if (queryBaseLocation == null) {
                queryBaseLocation = createURL("http://kasper-platform/kasper/query");
            }

            adapters.put(int.class, DefaultAdapters.numberAdapter);
            adapters.put(long.class, DefaultAdapters.numberAdapter);
            adapters.put(short.class, DefaultAdapters.numberAdapter);
            adapters.put(float.class, DefaultAdapters.numberAdapter);
            adapters.put(double.class, DefaultAdapters.numberAdapter);
            adapters.put(Number.class, DefaultAdapters.numberAdapter);
            adapters.put(Integer.class, DefaultAdapters.numberAdapter);
            adapters.put(Long.class, DefaultAdapters.numberAdapter);
            adapters.put(Short.class, DefaultAdapters.numberAdapter);
            adapters.put(Float.class, DefaultAdapters.numberAdapter);
            adapters.put(Double.class, DefaultAdapters.numberAdapter);
            adapters.put(String.class, DefaultAdapters.stringAdapter);
            adapters.put(Boolean.class, DefaultAdapters.booleanAdapter);
            adapters.put(boolean.class, DefaultAdapters.booleanAdapter);
            adapters.put(Date.class, DefaultAdapters.dateAdapter);
            adapters.put(DateTime.class, DefaultAdapters.dateTimeAdapter);

            factories.add(DefaultAdapters.collectionAdapterFactory);
            factories.add(DefaultAdapters.arrayAdapterFactory);

            if (queryFactory == null) {
                queryFactory = new StdQueryFactory(adapters, factories, VisibilityFilter.PACKAGE_PUBLIC);
            }

            if (client == null)
                return new KasperClient(queryFactory, mapper, commandBaseLocation, queryBaseLocation);
            else
                return new KasperClient(queryFactory, client, commandBaseLocation, queryBaseLocation);
        }

        private URL createURL(String url) {
            try {
                return new URL("http://kasper-platform/kasper/command");
            }
            catch (MalformedURLException e) {
                throw new KasperClientException(e);
            }
        }

        ObjectMapper defaultMapper() {
            Module kasperClientModule = new SimpleModule().addDeserializer(KasperCommandResult.class, new StdDeserializer<KasperCommandResult>(KasperCommandResult.class) {
                @Override
                public KasperCommandResult deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    Status status = null;

                    while (jp.nextToken() != JsonToken.END_OBJECT) {
                        String name = jp.getCurrentName();
                        jp.nextToken();
                        if ("status".equals(name)) {
                            status = jp.readValueAs(Status.class);
                        }
                        else {
                            // FIXME do we just ignore unknown properties or take some action?
                        }
                    }

                    return new KasperCommandResult(status);
                }
            });
            return new ObjectMapper().configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true)
                    .configure(MapperFeature.AUTO_DETECT_CREATORS, true)
                    .configure(MapperFeature.AUTO_DETECT_FIELDS, true)
                    .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true)
                    .configure(MapperFeature.USE_ANNOTATIONS, true)
                    .registerModule(kasperClientModule);
        }
    }
}
