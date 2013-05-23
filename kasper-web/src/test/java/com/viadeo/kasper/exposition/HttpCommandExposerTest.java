package com.viadeo.kasper.exposition;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.web.context.ContextLoaderListener;

import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.ICommandResult.Status;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.event.exceptions.KasperEventException;
import com.viadeo.kasper.platform.web.KasperPlatformBootListener;

public class HttpCommandExposerTest {
	private Server server;
	private int port;
	private KasperClient cli;

	@Before
	public void setUp() throws Exception {
		server = new Server(0);
		final ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.setContextPath("/");
		server.setHandler(servletContext);

		// ugly again :/ hard to test, hard to register a commandhandler for
		// testing purpose etc...
		servletContext.setInitParameter("contextConfigLocation",
				"classpath:spring/kasper/spring-kasper-platform.xml");
		servletContext.addEventListener(new ContextLoaderListener());
		servletContext.addEventListener(new KasperPlatformBootListener());
		servletContext.addServlet(new ServletHolder(new HttpCommandExposer()),
				"/rootpath/*");

		server.start();
		port = server.getConnectors()[0].getLocalPort();

		cli = new KasperClientBuilder().commandBaseLocation(
				new URL("http://localhost:" + port + "/rootpath/")).create();
	}

	@After
	public void cleanUp() throws Exception {
		server.stop();
	}

	@Test
	public void testCommandNotFound() throws Exception {
		// Given an unknown command
		@SuppressWarnings("serial")
		final ICommand unknownCommand = new ICommand() {
		};

		// When
		final ICommandResult result = cli.send(unknownCommand);

		// Then
		assertEquals(Status.ERROR, result.getStatus());
		assertNotNull(result.asError().getErrorMessage().orNull());
	}

	@Test
	public void testSuccessfulCommand() throws IOException, Exception {
		// Given valid input
		final CreateAccountCommand command = new CreateAccountCommand();
		command.name = "foo bar";

		// When
		final ICommandResult result = cli.send(command);

		// Then
		assertEquals(Status.OK, result.getStatus());
		assertEquals(command.name,
				CreateAccountCommandHandler.createAccountCommandName);
	}

	public static class CreateAccountCommand implements ICommand {
		private static final long serialVersionUID = 674842094873929150L;
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@XKasperCommandHandler(domain = AccountDomain.class)
	public static class CreateAccountCommandHandler extends
			AbstractCommandHandler<CreateAccountCommand> {
		static String createAccountCommandName = null;

		@Override
		public ICommandResult handle(CreateAccountCommand command)
				throws KasperEventException {
			createAccountCommandName = command.getName();
			return new KasperCommandResult(Status.OK);
		}
	}

	public static class AccountDomain extends AbstractDomain {

	}
}
