package com.viadeo.kasper.exposition;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import static com.google.common.base.Preconditions.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.impl.KasperErrorCommandResult;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import com.viadeo.kasper.platform.IPlatform;
import com.viadeo.kasper.tools.ObjectMapperProvider;

public final class HttpPublisher extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895624L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpPublisher.class);

	private final Map<String, Class<? extends IQuery>> exposedQueries = new HashMap<String, Class<? extends IQuery>>();
	private final Map<String, Class<? extends ICommand>> exposedCommands = new HashMap<String, Class<? extends ICommand>>();
	private IQueryServicesLocator queryServicesLocator;
	private IDomainLocator domainLocator;
	private IPlatform platform;

	public HttpPublisher() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// ugly :/
		WebApplicationContext ctx = checkNotNull(WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext()));
		platform = checkNotNull(ctx.getBean(IPlatform.class));
		queryServicesLocator = checkNotNull(ctx
				.getBean(IQueryServicesLocator.class));
		domainLocator = checkNotNull(ctx.getBean(IDomainLocator.class));

		// expose all registered queries and commands
		for (IQueryService<? extends IQuery, ? extends IQueryDTO> queryService : queryServicesLocator
				.getServices()) {
			expose(queryService);
		}

		for (ICommandHandler<? extends ICommand> handler : domainLocator
				.getHandlers()) {
			expose(handler);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleCommand(req, resp);

		// must be last call to ensure that everything is sent to the client
		// (even if an error occurred)
		resp.flushBuffer();
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleCommand(req, resp);

		// must be last call to ensure that everything is sent to the client
		// (even if an error occurred)
		resp.flushBuffer();
	}

	private void handleCommand(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// always respond with a json stream (even if empty)
		resp.setContentType("application/json; charset=utf-8");

		// FIXME can throw an error ensure to respond a json stream
		String commandName = resourceName(req.getRequestURI());
		// locate corresponding command class
		Class<? extends ICommand> commandClass = exposedCommands
				.get(commandName);
		if (commandClass == null) {
			sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Command["
					+ commandName + "] not found.");
			return;
		}

		ICommandResult result = null;

		JsonParser parser = null;

		try {
			if (!req.getContentType().startsWith("application/json")) {
				sendJsonError(resp, HttpServletResponse.SC_NOT_ACCEPTABLE,
						"Accepting and producing only application/json");
				return;
			}

			ObjectReader reader = ObjectMapperProvider.instance.objectReader();
			// parse the input stream to that command
			parser = reader.getFactory().createJsonParser(req.getInputStream());
			ICommand command = reader.readValue(parser, commandClass);

			// FIXME 1 use context from request
			// FIXME 2 does it make sense to have async commands here? In any
			// case the user is expecting a result success or failure

			// send now that command to the platform and wait for the result
			result = platform.getCommandGateway().sendCommandAndWaitForAResult(
					command, new DefaultContextBuilder().buildDefault());
		} catch (JsonParseException e) {
			LOGGER.error(
					"Error parse command [" + commandClass.getName() + "]", e);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"The command could not be parsed.");
		} catch (IOException e) {
			LOGGER.error(
					"Error parse command [" + commandClass.getName() + "]", e);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"The command could not be parsed.");
		} catch (Throwable th) {
			// we catch any other exception in order to still respond with json
			LOGGER.error("Error for command [" + commandClass.getName() + "]",
					th);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error occured preventing us from handling your command.");
		} finally {
			if (parser != null) {
				/*
				 * FIXME check if jackson is closing the underlying inputstream,
				 * we still must close it inorder to allow Jackson to recycle
				 * its buffers
				 */
				parser.close();
			}
		}

		// if the result is null this means that we handled the error previously
		// so nothing can be done anymore
		if (result != null)
			sendResponse(result, resp, commandClass);
	}

	/**
	 * Will try to send an error by setting the right http status and writing a
	 * json response in the body. If it fails there will be no json body.
	 */
	/*
	 * we need to use setStatus because sendError commits the response,
	 * preventing us from writing the respone as json it also forces response to
	 * text/html.
	 */
	@SuppressWarnings("deprecation")
	private void sendJsonError(HttpServletResponse response, int status,
			String reason) throws JsonGenerationException,
			JsonMappingException, IOException {
		// set an error status and a message
		response.setStatus(status, reason);
		// write also into the body the result as json
		ObjectMapperProvider.instance.objectWriter().writeValue(
				response.getOutputStream(),
				new KasperErrorCommandResult(reason));
	}

	private void sendResponse(ICommandResult result, HttpServletResponse resp,
			Class<? extends ICommand> commandClass) throws IOException {
		ObjectReader reader = ObjectMapperProvider.instance.objectReader();
		JsonGenerator generator = null;

		try {
			// try writing the response
			generator = reader.getFactory().createJsonGenerator(
					resp.getOutputStream());
			ObjectMapperProvider.instance.objectWriter().writeValue(generator,
					result);
			/*
			 * FIXME how to handle errors here, jackson started writing so we
			 * don't have any guarantees about the content that has been up to
			 * now
			 */
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (JsonGenerationException e) {
			LOGGER.error("Error outputing command result to json for command ["
					+ commandClass.getName() + "] and result [" + result + "]",
					e);
		} catch (JsonMappingException e) {
			LOGGER.error("Error mapping command result to json for command ["
					+ commandClass.getName() + "] and result [" + result + "]",
					e);
		} catch (IOException e) {
			LOGGER.error("Error outputing command result to json for command ["
					+ commandClass.getName() + "] and result [" + result + "]",
					e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error occured during the generation of the response.");
		} finally {
			if (generator != null) {
				generator.flush();
				generator.close();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	HttpPublisher expose(
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	HttpPublisher expose(
			final ICommandHandler<? extends ICommand> commandHandler) {
		checkNotNull(commandHandler);
		final TypeToken<? extends ICommandHandler> typeToken = TypeToken
				.of(commandHandler.getClass());
		final Class<? super ICommand> commandClass = (Class<? super ICommand>) typeToken
				.getSupertype(ICommandHandler.class)
				.resolveType(ICommandHandler.class.getTypeParameters()[0])
				.getRawType();
		_putKey(commandToPath(commandClass), commandClass, exposedCommands);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void _putKey(final String key, final Class newValue,
			final Map mapping) {
		final Class<?> value = (Class<?>) mapping.get(key);
		if (value != null)
			throw new IllegalArgumentException("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
		mapping.put(key, newValue);
	}

	private String resourceName(String uri) {
		checkNotNull(uri);
		int idx = uri.lastIndexOf('/');
		if (idx > -1) {
			return uri.substring(idx + 1);
		} else {
			return uri;
		}
	}

	private String queryToPath(final Class<? super IQuery> exposedQuery) {
		return exposedQuery.getSimpleName().replaceAll("Query", "");
	}

	private String commandToPath(final Class<? super ICommand> exposedCommand) {
		return exposedCommand.getSimpleName().replaceAll("Command", "");
	}

	void setQueryServicesLocator(IQueryServicesLocator queryServicesLocator) {
		this.queryServicesLocator = queryServicesLocator;
	}
}
