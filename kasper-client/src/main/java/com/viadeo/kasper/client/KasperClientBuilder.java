// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.query.exposition.IQueryFactory;
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.ITypeAdapterFactory;
import com.viadeo.kasper.query.exposition.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.VisibilityFilter;
import com.viadeo.kasper.tools.ObjectMapperProvider;

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
    private IQueryFactory queryFactory;
    private final QueryFactoryBuilder qFactoryBuilder = new QueryFactoryBuilder();

    // ------------------------------------------------------------------------

    private static final String DEFAULT_COMMAND_URL = "http://kasper-platform/kasper/command";
    private static final String DEFAULT_QUERY_URL = "http://kasper-platform/kasper/query";

    // ------------------------------------------------------------------------

    // lets forbid mapper configuration as we need to use the same one on both client and server side.
    KasperClientBuilder use(final ObjectMapper mapper) {
        checkNotNull(mapper);
        this.mapper = mapper;
        return this;
    }

    KasperClientBuilder use(final IQueryFactory queryFactory) {
        checkNotNull(queryFactory);
        this.queryFactory = queryFactory;
        return this;
    }

    /**
     * Registers an adapter for its parameterized type for query ser/deser. Registration of adapters should be done in
     * domain api projects as they are shared between server and clients. To allow adapters discovery prefer using java
     * service loader mechanism. Create a file named com.viadeo.kasper.query.exposition.ITypeAdapter under
     * META-INF/services and put inside the name of your adapters. They will be automatically discovered by the
     * KasperClient.
     * 
     * @param adapter to register for query serialization/deserialization.
     * @return a reference to this builder.
     * 
     * @see ITypeAdapter
     */
    public KasperClientBuilder use(final ITypeAdapter<?> adapter) {
        qFactoryBuilder.use(adapter);
        return this;
    }

    /**
     * @see #use(ITypeAdapter)
     */
    public KasperClientBuilder use(final ITypeAdapterFactory<?> factory) {
        qFactoryBuilder.use(factory);
        return this;
    }

    // ------------------------------------------------------------------------
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

    // again does it make sense to make it configurable when we know that the configuration must be shared between client and server
    KasperClientBuilder include(VisibilityFilter visibility) {
        qFactoryBuilder.include(visibility);
        return this;
    }

    // maybe make it public?
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

        if (queryFactory == null) {
            queryFactory = qFactoryBuilder.create();
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
        return ObjectMapperProvider.instance.mapper();
    }
}
