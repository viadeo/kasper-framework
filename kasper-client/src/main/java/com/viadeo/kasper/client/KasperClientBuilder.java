// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.databind.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.client.lib.DefaultTypeAdapters;
import com.viadeo.kasper.client.lib.IQueryFactory;
import com.viadeo.kasper.client.lib.ITypeAdapter;
import com.viadeo.kasper.client.lib.ITypeAdapterFactory;
import com.viadeo.kasper.client.lib.KasperCommandResultDeserializer;
import com.viadeo.kasper.client.lib.StdQueryFactory;
import com.viadeo.kasper.client.lib.VisibilityFilter;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;

public class KasperClientBuilder {
    static final Logger LOGGER = LoggerFactory.getLogger(KasperClientBuilder.class);

    private Client client;
    private ObjectMapper mapper;
    private URL commandBaseLocation;
    private URL queryBaseLocation;
    private IQueryFactory queryFactory;
    private ConcurrentMap<Type, ITypeAdapter<?>> adapters = Maps.newConcurrentMap();
    private List<ITypeAdapterFactory<?>> factories = Lists.newArrayList();

    // ------------------------------------------------------------------------

    private static final String DEFAULT_COMMAND_URL = "http://kasper-platform/kasper/command";
    private static final String DEFAULT_QUERY_URL = "http://kasper-platform/kasper/query";
    
    private static final Class<?>[] NUMBER_ADAPTED_CLASSES = new Class<?>[] {
        int.class, long.class, short.class, float.class, double.class,
        Number.class, Integer.class, Long.class, Short.class, Float.class, Double.class
    };

    // ------------------------------------------------------------------------

    public KasperClientBuilder use(final ObjectMapper mapper) {
        checkNotNull(mapper);
        this.mapper = mapper;
        return this;
    }

    public KasperClientBuilder use(final IQueryFactory queryFactory) {
        checkNotNull(queryFactory);
        this.queryFactory = queryFactory;
        return this;
    }

    @SuppressWarnings("unchecked")
    public KasperClientBuilder use(final ITypeAdapter<?> adapter) {
        checkNotNull(adapter);
        TypeToken<?> adapterForType = TypeToken.of(adapter.getClass()).getSupertype(ITypeAdapter.class).resolveType(ITypeAdapter.class.getTypeParameters()[0]);
        adapters.put(adapterForType.getType(), (ITypeAdapter<Object>) adapter);
        return this;
    }

    public KasperClientBuilder use(final ITypeAdapterFactory<?> factory) {
        factories.add(checkNotNull(factory));
        return this;
    }

    // ------------------------------------------------------------------------    

    public KasperClientBuilder queryBaseLocation(final URL url) {
        queryBaseLocation = checkNotNull(url);
        return this;
    }

    public KasperClientBuilder commandBaseLocation(final URL url) {
        commandBaseLocation = checkNotNull(url);
        return this;
    }

    // maybe make it public?
    KasperClientBuilder client(final Client client) {
        this.client = checkNotNull(client);
        return this;
    }

    // ------------------------------------------------------------------------

    public KasperClient create() {

        if (null == mapper) {
            mapper = defaultMapper();
        }

        if (null == commandBaseLocation) {
            commandBaseLocation = createURL(DEFAULT_COMMAND_URL);
        }

        if (null == queryBaseLocation) {
            queryBaseLocation = createURL(DEFAULT_QUERY_URL);
        }

        for (final Class<?> numberAdaptedClass : NUMBER_ADAPTED_CLASSES) {
            adapters.putIfAbsent(numberAdaptedClass, DefaultTypeAdapters.NUMBER_ADAPTER);
        }

        adapters.putIfAbsent(String.class, DefaultTypeAdapters.STRING_ADAPTER);
        adapters.putIfAbsent(Boolean.class, DefaultTypeAdapters.BOOLEAN_ADAPTER);
        adapters.putIfAbsent(boolean.class, DefaultTypeAdapters.BOOLEAN_ADAPTER);
        adapters.putIfAbsent(Date.class, DefaultTypeAdapters.DATE_ADAPTER);
        adapters.putIfAbsent(DateTime.class, DefaultTypeAdapters.DATETIME_ADAPTER);

        factories.add(DefaultTypeAdapters.COLLECTION_ADAPTER_FACTORY);
        factories.add(DefaultTypeAdapters.ARRAY_ADAPTER_FACTORY);

        if (null == queryFactory) {
            queryFactory = new StdQueryFactory(adapters, factories, VisibilityFilter.PACKAGE_PUBLIC);
        }

        if (null == client) {
            return new KasperClient(queryFactory, mapper, commandBaseLocation, queryBaseLocation);
        } else {
            return new KasperClient(queryFactory, client, commandBaseLocation, queryBaseLocation);
        }
    }

    // ------------------------------------------------------------------------

    // FIXME: non-used parameter ?
    private URL createURL(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new KasperClientException(e);
        }
    }

    // ------------------------------------------------------------------------

    ObjectMapper defaultMapper() {
        final Module kasperClientModule = new SimpleModule()
                .addDeserializer(KasperCommandResult.class, new KasperCommandResultDeserializer());

        return new ObjectMapper().configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true)
                .configure(MapperFeature.AUTO_DETECT_CREATORS, true)
                .configure(MapperFeature.AUTO_DETECT_FIELDS, true)
                .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true)
                .configure(MapperFeature.USE_ANNOTATIONS, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(kasperClientModule);
    }

}
