// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
import com.google.common.base.Optional;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.exposition.FeatureConfiguration;
import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.adapters.TypeAdapterFactory;
import com.viadeo.kasper.common.exposition.query.BeanAdapter;
import com.viadeo.kasper.common.exposition.query.QueryFactory;
import com.viadeo.kasper.common.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.common.exposition.query.VisibilityFilter;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;

import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This builder must be used if you want to change KasperClient configuration. By default all queries will be sent to
 * {@value #DEFAULT_QUERY_URL} url and commands to {@value #DEFAULT_COMMAND_URL}.
 *
 * Queries can have attributes of primitive types, arrays, collections and some date types (including jodatime). If some
 * type is missing just open an issue and it will be added.
 */
public class KasperClientBuilder {

    private final QueryFactoryBuilder qFactoryBuilder = new QueryFactoryBuilder();

    private int numberOfRetries;
    private Optional<RetryFilter> optionalRetryFilter;

    private Client client;
    private ObjectMapper mapper;
    private URL commandBaseLocation;
    private URL queryBaseLocation;
    private URL eventBaseLocation;
    private QueryFactory queryFactory;
    private HttpContextSerializer contextSerializer;
    private KasperClient.Flags flags;

    // ------------------------------------------------------------------------

    public static final String DEFAULT_COMMAND_URL = "http://localhost:8080/command";
    public static final String DEFAULT_QUERY_URL = "http://localhost:8080/query";
    public static final String DEFAULT_EVENT_URL = "http://localhost:8080/event";

    // ------------------------------------------------------------------------

    public KasperClientBuilder() {
        this.numberOfRetries = 3;
        this.optionalRetryFilter = Optional.of(new RetryFilter(numberOfRetries));
        this.flags = KasperClient.Flags.defaults();
    }

    /**
     * Registers an adapter for its parameterized type for query ser/deser. Registration of adapters should be done in
     * domain api projects as they are shared between server and clients. To allow adapters discovery prefer using java
     * service loader mechanism. Create a file named com.viadeo.kasper.common.exposition.TypeAdapter under
     * META-INF/services and put inside the name of your adapters. They will be automatically discovered by the
     * KasperClient.
     * 
     * @param mapper to register for query serialization/deserialization.
     * @return a reference to this builder.
     * @see com.viadeo.kasper.common.exposition.TypeAdapter
     */
    public KasperClientBuilder use(final ObjectMapper mapper) {
        this.mapper = checkNotNull(mapper);
        return this;
    }

    public KasperClientBuilder use(final QueryFactory queryFactory) {
        this.queryFactory = checkNotNull(queryFactory);
        return this;
    }

    public KasperClientBuilder features(final FeatureConfiguration features) {
        this.qFactoryBuilder.use(checkNotNull(features));
        return this;
    }

    public KasperClientBuilder use(final TypeAdapter adapter) {
        qFactoryBuilder.use(checkNotNull(adapter));
        return this;
    }

    public KasperClientBuilder use(final BeanAdapter beanAdapter) {
        qFactoryBuilder.use(checkNotNull(beanAdapter));
        return this;
    }

    /**
     * @param factory a factory
     * @return a reference to this builder.
     * @see #use(com.viadeo.kasper.common.exposition.TypeAdapter)
     */
    public KasperClientBuilder use(final TypeAdapterFactory factory) {
        qFactoryBuilder.use(checkNotNull(factory));
        return this;
    }

    /**
     * The client will retry to submit something if some exception
     * occurs
     *
     * @param numberOfRetries number of times to retry
     * @return a reference to this builder
     */
    public KasperClientBuilder numberOfRetries(final int numberOfRetries) {
        checkArgument(numberOfRetries > 0);
        this.numberOfRetries = numberOfRetries;

        final RetryFilter retryFilter = new RetryFilter(numberOfRetries);

        if (optionalRetryFilter.isPresent() && null != client) {
            client.removeFilter(optionalRetryFilter.get());
            client.addFilter(retryFilter);
        }

        this.optionalRetryFilter = Optional.of(retryFilter);
        return this;
    }

    // ------------------------------------------------------------------------
    /**
     * @param url of the base path to use for query submission.
     * @return a reference to this builder.
     * @throws KasperException a kasper exception
     */
    public KasperClientBuilder queryBaseLocation(final String url) {
        return queryBaseLocation(createURL(getCanonicalUrl(checkNotNull(url))));
    }

    /**
     * @param url of the base path to use for commands submission.
     * @return a reference to this builder.
     * @throws KasperException a kasper exception
     */
    public KasperClientBuilder commandBaseLocation(final String url) {
        return commandBaseLocation(createURL(getCanonicalUrl(checkNotNull(url))));
    }

    /**
     * @param url of the base path to use for event submission.
     * @return a reference to this builder.
     * @throws KasperException a kasper exception
     */
    public KasperClientBuilder eventBaseLocation(final String url) {
        return eventBaseLocation(createURL(getCanonicalUrl(checkNotNull(url))));
    }

    /**
     * Add a trailing "/" at the end of the base URL.
     * Also check url is not null as a precondition
     * In case of URL without trailing "/", the last part of it is removed by java.net.URL constructor otherwise
     * @param url the base URL
     * @return url plus trailing "/"
     */
    private String getCanonicalUrl(final String url) {
        return checkNotNull(url).endsWith("/") ? url : url + "/";
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

    /**
     * @param url of the base path to use for event submission.
     * @return a reference to this builder.
     */
    public KasperClientBuilder eventBaseLocation(final URL url) {
        eventBaseLocation = checkNotNull(url);
        return this;
    }

    public KasperClientBuilder include(final VisibilityFilter visibility) {
        qFactoryBuilder.include(visibility);
        return this;
    }

    public KasperClientBuilder usePostForQueries(final boolean enabled) {
        flags.usePostForQueries(enabled);
        return this;
    }

    public KasperClientBuilder useFlags(final KasperClient.Flags flags) {
        this.flags.importFrom(flags);
        return this;
    }

    public KasperClientBuilder contextSerializer(final HttpContextSerializer contextSerializer) {
        this.contextSerializer = contextSerializer;
        return this;
    }

    public KasperClientBuilder client(final Client client) {
        this.client = checkNotNull(client);

        if (optionalRetryFilter.isPresent() && ! client.isFilterPreset(optionalRetryFilter.get())) {
            this.client.addFilter(optionalRetryFilter.get());
        }

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

        if (null == eventBaseLocation) {
            eventBaseLocation = createURL(DEFAULT_EVENT_URL);
        }

        if (null == queryFactory) {
            queryFactory = qFactoryBuilder.create();
        }

        if (null == contextSerializer) {
            contextSerializer = new HttpContextSerializer();
        }

        if (null == client) {
            final DefaultClientConfig cfg = new DefaultClientConfig();
            cfg.getSingletons().add(new JacksonJsonProvider(mapper));
            client = Client.create(cfg);

            if (optionalRetryFilter.isPresent()) {
                client.addFilter(optionalRetryFilter.get());
            }
        }

        return new KasperClient(
                queryFactory,
                client,
                commandBaseLocation,
                queryBaseLocation,
                eventBaseLocation,
                contextSerializer,
                flags
        );

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

    // ------------------------------------------------------------------------

    @VisibleForTesting
    protected Optional<RetryFilter> getOptionalRetryFilter() {
        return optionalRetryFilter;
    }
}
