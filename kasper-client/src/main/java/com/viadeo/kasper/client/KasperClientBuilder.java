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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.query.exposition.IQueryFactory;
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.ITypeAdapterFactory;
import com.viadeo.kasper.query.exposition.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.VisibilityFilter;
import com.viadeo.kasper.tools.ObjectMapperProvider;

public class KasperClientBuilder {
	static final Logger LOGGER = LoggerFactory
			.getLogger(KasperClientBuilder.class);

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

	public KasperClientBuilder use(final ITypeAdapter<?> adapter) {
		qFactoryBuilder.use(adapter);
		return this;
	}

	public KasperClientBuilder use(final ITypeAdapterFactory<?> factory) {
		qFactoryBuilder.use(factory);
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

	public KasperClientBuilder include(VisibilityFilter visibility) {
		qFactoryBuilder.include(visibility);
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

		if (queryFactory == null) {
			queryFactory = qFactoryBuilder.create();
		}

		if (null == client) {
			return new KasperClient(queryFactory, mapper, commandBaseLocation,
					queryBaseLocation);
		} else {
			return new KasperClient(queryFactory, client, commandBaseLocation,
					queryBaseLocation);
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
