package com.viadeo.kasper.exposition;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import com.viadeo.kasper.platform.IPlatform;
import com.viadeo.kasper.query.exposition.IQueryFactory;
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.QueryFactoryBuilder;
import com.viadeo.kasper.query.exposition.QueryParser;
import com.viadeo.kasper.tools.ObjectMapperProvider;

public class HttpQueryExposer extends HttpExposer {
	private static final long serialVersionUID = 8448984922303895624L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpQueryExposer.class);

	private final Map<String, Class<? extends IQuery>> exposedQueries = new HashMap<String, Class<? extends IQuery>>();
	private IQueryServicesLocator queryServicesLocator;
	private IQueryFactory queryAdapterFactory = new QueryFactoryBuilder()
			.create();

	public HttpQueryExposer() {
	}

	@Override
	protected void configure(IPlatform platform, WebApplicationContext context) {
		queryServicesLocator = checkNotNull(context
				.getBean(IQueryServicesLocator.class));

		// expose all registered queries and commands
		for (IQueryService<? extends IQuery, ? extends IQueryDTO> queryService : queryServicesLocator
				.getServices()) {
			expose(queryService);
		}
	}

	// again can not use sendError
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		// TODO we should think of providing some more information to client in
		// case of failure. We also need to be sure that those infos a really
		// useful

		// always respond with a json stream (even if empty)
		resp.setContentType("application/json; charset=utf-8");

		// lets be very defensive and catch every thing in order to not break
		// the contract with clients = JSON only
		try {
			final String queryName = resourceName(req.getRequestURI());

			final IQuery query = parseQuery(queryName, req, resp);
			IQueryDTO dto = null;

			if (!resp.isCommitted()) {
				dto = handleQuery(queryName, query, resp);
			}

			// need to check again as something might go wrong in handleQuery
			if (!resp.isCommitted()) {
				sendDTO(queryName, dto, resp);
			}
		} catch (Throwable t) {
			sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Could not handle query[" + req.getRequestURI()
							+ "] and parameters[" + req.getQueryString() + "]",
					resp, t);

		}

		resp.flushBuffer();
	}

	// we can not use send error as it will send text/html response.
	protected IQuery parseQuery(final String queryName,
			final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {

		IQuery query = null;
		final Class<? extends IQuery> queryClass = exposedQueries
				.get(queryName);

		if (queryClass == null) {
			sendError(HttpServletResponse.SC_NOT_FOUND, "No such query["
					+ queryName + "].", resp, null);
		} else {
			final ITypeAdapter<? extends IQuery> adapter = queryAdapterFactory
					.create(TypeToken.of(queryClass));

			final Map<String, List<String>> queryParams = new HashMap<String, List<String>>();

			for (final Enumeration<String> keys = req.getParameterNames(); keys
					.hasMoreElements();) {
				final String key = keys.nextElement();
				queryParams
						.put(key, Arrays.asList(req.getParameterValues(key)));
			}

			try {
				query = adapter.adapt(new QueryParser(queryParams));
			} catch (Throwable t) {
				// OK lets catch any exception that could occur during
				// deserialization and try to send back
				sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Unable to parse Query[" + queryName
								+ "] and parameters [" + req.getQueryString()
								+ "].", resp, t);
			}
		}

		return query;
	}

	// can not use sendError it is forcing response to text/html
	protected IQueryDTO handleQuery(String queryName, IQuery query,
			HttpServletResponse resp) throws IOException {
		IQueryDTO dto = null;
		try {
			// checking if the response has not been sent, if it is true this
			// means that an error happened and has been handled
			dto = platform().getQueryGateway().retrieve(
					new DefaultContextBuilder().buildDefault(), query);
		} catch (Throwable e) {
			// it is ok to eat all kind of exceptions as they occur at parsing
			// level so we know what approximatively failed.
			sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR Submiting query[" + queryName
							+ "] to Kasper platform.", resp, e);
		}
		return dto;
	}

	// can not use sendError it is forcing response to text/html
	protected void sendDTO(String queryName, IQueryDTO dto,
			HttpServletResponse resp) throws IOException {
		ObjectWriter writer = ObjectMapperProvider.instance.objectWriter();
		try {
			writer.writeValue(resp.getOutputStream(), dto);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Throwable t) {
			sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR sending DTO[" + dto.getClass().getSimpleName()
							+ "] for query[" + queryName + "].", resp, t);
		}
	}

	@SuppressWarnings("deprecation")
	private void sendError(int status, String message,
			HttpServletResponse resp, Throwable exception) throws IOException {
		if (exception != null)
			LOGGER.error(message, exception);
		else
			LOGGER.error(message);

		resp.setStatus(status, message);

		ObjectWriter writer = ObjectMapperProvider.instance.objectWriter();
		JsonGenerator generator = writer.getJsonFactory().createGenerator(
				resp.getOutputStream());

		try {
			generator.writeStartObject();
			// FIXME for the moment lets just put the minimum here
			generator.writeNumberField("code", status);
			generator.writeStringField("reason", message);
			generator.writeEndObject();
		} finally {
			generator.close();
		}

		resp.flushBuffer();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	HttpQueryExposer expose(
			final IQueryService<? extends IQuery, ? extends IQueryDTO> queryService) {
		checkNotNull(queryService);
		final TypeToken<? extends IQueryService> typeToken = TypeToken
				.of(queryService.getClass());
		final Class<? super IQuery> queryClass = (Class<? super IQuery>) typeToken
				.getSupertype(IQueryService.class)
				.resolveType(IQueryService.class.getTypeParameters()[0])
				.getRawType();
		_putKey(queryToPath(queryClass), queryClass, exposedQueries);
		return this;
	}

	private String queryToPath(final Class<? super IQuery> exposedQuery) {
		return exposedQuery.getSimpleName().replaceAll("Query", "");
	}
}
