// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.query.exposition.FeatureConfiguration;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.adapters.TypeAdapterFactory;
import com.viadeo.kasper.query.exposition.query.QueryFactory;
import com.viadeo.kasper.query.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.query.VisibilityFilter;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This builder must be used if you want to change KasperClient configuration. By default all queries will be sent to
 * {@value #DEFAULT_QUERY_URL} url and commands to {@value #DEFAULT_COMMAND_URL}. <br/>
 * Queries can have attributes of primitive types, arrays, collections and some date types (including jodatime). If some
 * type is missing just open an issue and it will be added.
 */
public class KasperClientBuilder {

    private Client client;
    private ObjectMapper mapper;
    private URL commandBaseLocation;
    private URL queryBaseLocation;
    private QueryFactory queryFactory;
    private final QueryFactoryBuilder qFactoryBuilder = new QueryFactoryBuilder();
    private boolean usePostForQueries;

    // ------------------------------------------------------------------------

    public static final String DEFAULT_COMMAND_URL = "http://localhost:8080/command";
    public static final String DEFAULT_QUERY_URL = "http://localhost:8080/query";

    // ------------------------------------------------------------------------

    /**
     * Registers an adapter for its parameterized type for query ser/deser. Registration of adapters should be done in
     * domain api projects as they are shared between server and clients. To allow adapters discovery prefer using java
     * service loader mechanism. Create a file named com.viadeo.kasper.query.exposition.TypeAdapter under
     * META-INF/services and put inside the name of your adapters. They will be automatically discovered by the
     * KasperClient.
     * 
     * @param mapper to register for query serialization/deserialization.
     * @return a reference to this builder.
     * @see com.viadeo.kasper.query.exposition.TypeAdapter
     */
    public KasperClientBuilder use(final ObjectMapper mapper) {
        checkNotNull(mapper);
        this.mapper = mapper;
        return this;
    }

    public KasperClientBuilder use(final QueryFactory queryFactory) {
        checkNotNull(queryFactory);
        this.queryFactory = queryFactory;
        return this;
    }

    public KasperClientBuilder features(final FeatureConfiguration features) {
        this.qFactoryBuilder.use(features);
        return this;
    }

    public KasperClientBuilder use(final TypeAdapter<?> adapter) {
        qFactoryBuilder.use(adapter);
        return this;
    }

    /**
     * @see #use(com.viadeo.kasper.query.exposition.TypeAdapter)
     */
    public KasperClientBuilder use(final TypeAdapterFactory<?> factory) {
        qFactoryBuilder.use(factory);
        return this;
    }

    // ------------------------------------------------------------------------
    /**
     * @param url of the base path to use for query submission.
     * @return a reference to this builder.
     * @throws KasperException
     */
    public KasperClientBuilder queryBaseLocation(final String url) {
        return queryBaseLocation(createURL(checkNotNull(url)));
    }

    /**
     * @param url of the base path to use for commands submission.
     * @return a reference to this builder.
     * @throws KasperException
     */
    public KasperClientBuilder commandBaseLocation(final String url) {
        return commandBaseLocation(createURL(checkNotNull(url)));
    }

    /**
     * @param url of the base path to use for query submission.
     * @return a reference to this builder.
     */
    public KasperClientBuilder queryBaseLocation(final URL url) {
        queryBaseLocation = checkNotNull(url);
        return this;
    }

    /**
     * @param url of the base path to use for commands submission.
     * @return a reference to this builder.
     */
    public KasperClientBuilder commandBaseLocation(final URL url) {
        commandBaseLocation = checkNotNull(url);
        return this;
    }

    public KasperClientBuilder include(final VisibilityFilter visibility) {
        qFactoryBuilder.include(visibility);
        return this;
    }

    public KasperClientBuilder usePostForQueries(final boolean enabled) {
        usePostForQueries = enabled;
        return this;
    }

    // FIXME: maybe make it public?
    KasperClientBuilder client(final Client client) {
        this.client = checkNotNull(client);
        return this;
    }

    // ------------------------------------------------------------------------

    /**
     * @return a instance of a new KasperClient for that configuration.
     */
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

        if (null == queryFactory) {
            queryFactory = qFactoryBuilder.create();
        }

        if (null == client) {
            return new KasperClient(queryFactory, mapper, commandBaseLocation, queryBaseLocation, usePostForQueries);
        } else {
            return new KasperClient(queryFactory, client, commandBaseLocation, queryBaseLocation, usePostForQueries);
        }
    }

    // ------------------------------------------------------------------------
    
    private URL createURL(final String url) {
        try {

            return new URL(url);

        } catch (final MalformedURLException e) {
            throw new KasperException("Bad URL[" + url + "]", e);
        }
    }

    // ------------------------------------------------------------------------

    ObjectMapper defaultMapper() {
        return ObjectMapperProvider.INSTANCE.mapper();
    }

}
