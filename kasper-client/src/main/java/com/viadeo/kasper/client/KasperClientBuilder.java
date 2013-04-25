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
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.client.lib.DefaultTypeAdapters;
import com.viadeo.kasper.client.lib.IQueryFactory;
import com.viadeo.kasper.client.lib.ITypeAdapterFactory;
import com.viadeo.kasper.client.lib.KasperCommandResultDeserializer;
import com.viadeo.kasper.client.lib.StdQueryFactory;
import com.viadeo.kasper.client.lib.TypeAdapter;
import com.viadeo.kasper.client.lib.VisibilityFilter;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public class KasperClientBuilder {
    static final Logger LOGGER = LoggerFactory.getLogger(KasperClientBuilder.class);
    
    private Client client;
    private ObjectMapper mapper;
    private URL commandBaseLocation;
    private URL queryBaseLocation;
    private IQueryFactory queryFactory;
    private Map<Type, TypeAdapter<?>> adapters = Maps.newHashMap();
    private List<ITypeAdapterFactory> factories = Lists.newArrayList();

    // ------------------------------------------------------------------------
    
    private static final String DEFAULT_COMMAND_URL = "http://kasper-platform/kasper/command";
    
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

    public KasperClientBuilder use(final TypeAdapter<?> adapter) {
        checkNotNull(adapter);
        adapters.put(ReflectionGenericsResolver.getParameterTypeFromClass(adapter.getClass(), TypeAdapter.class, 0).get(), adapter);
        return this;
    }

    public KasperClientBuilder use(final ITypeAdapterFactory factory) {
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
            commandBaseLocation = createURL("http://kasper-platform/kasper/command");
        }
        
        if (null == queryBaseLocation) {
            queryBaseLocation = createURL("http://kasper-platform/kasper/query");
        }

        for (final Class<?> numberAdaptedClass : NUMBER_ADAPTED_CLASSES) {
        	adapters.put(numberAdaptedClass, DefaultTypeAdapters.NUMBER_ADAPTER);    
        }
        
        adapters.put(String.class, DefaultTypeAdapters.STRING_ADAPTER);
        adapters.put(Boolean.class, DefaultTypeAdapters.BOOLEAN_ADAPTER);
        adapters.put(boolean.class, DefaultTypeAdapters.BOOLEAN_ADAPTER);
        adapters.put(Date.class, DefaultTypeAdapters.DATE_ADAPTER);
        adapters.put(DateTime.class, DefaultTypeAdapters.DATETIME_ADAPTER);

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
            return new URL(DEFAULT_COMMAND_URL);
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
                .registerModule(kasperClientModule);
    }
    
}