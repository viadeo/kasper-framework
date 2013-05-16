package com.viadeo.kasper.exposition;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import com.viadeo.kasper.tools.ObjectMapperProvider;

public class PublisherTest {
	private ServletTester tester;
	private HttpTester request;
	private Server server;
	private int port;

	@Before
	public void setUp() throws Exception {
		server = new Server(0);
		ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.setContextPath("/");
		server.setHandler(servletContext);

		servletContext.setInitParameter("contextConfigLocation",
				"classpath:spring/kasper/spring-kasper-platform.xml");
		servletContext.addEventListener(new ContextLoaderListener());
		servletContext.addEventListener(new KasperPlatformBootListener());
		servletContext.addServlet(new ServletHolder(new HttpPublisher()),
				"/rootpath/*");

		server.start();
		port = server.getConnectors()[0].getLocalPort();
	}

	@After
	public void cleanUp() throws Exception {
		server.stop();
	}

	@Test
	public void testCommandNotFound() throws Exception {
		KasperClient cli = new KasperClientBuilder().commandBaseLocation(
				new URL("http://localhost:" + port + "/rootpath/DoesNotExist"))
				.create();
		@SuppressWarnings("serial")
		ICommandResult result = cli.send(new ICommand() {});
		System.out.println("============\n"+result);
	}

//	@Test
	public void testSuccessfulCommand() throws IOException, Exception {
		// given valid input
		HttpTester response = new HttpTester();
		CreateAccountCommand command = new CreateAccountCommand();
		command.name = "foo bar";
		String jsonRequest = ObjectMapperProvider.instance.objectWriter()
				.writeValueAsString(command);
		request.setContent(jsonRequest);

		// when
		System.out.println(response.parse(tester.getResponses(request
				.generate())));
		System.out.println(response.getContent());
	}

	static class CreateAccountCommand implements ICommand {
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
	static class CreateAccountCommandHandler extends
			AbstractCommandHandler<CreateAccountCommand> {
		@Override
		public ICommandResult handle(CreateAccountCommand command)
				throws KasperEventException {
			return new KasperCommandResult(Status.OK);
		}
	}

	static class AccountDomain extends AbstractDomain {

	}
}
