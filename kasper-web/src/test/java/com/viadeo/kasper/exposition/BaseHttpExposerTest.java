package com.viadeo.kasper.exposition;

import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.springframework.web.context.ContextLoaderListener;

import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.platform.web.KasperPlatformBootListener;

public class BaseHttpExposerTest<T extends HttpExposer> {
	private final Class<T> testedExposer;
	private Server server;
	private int port;
	private KasperClient cli;

	protected BaseHttpExposerTest(Class<T> testedExposer) {
		this.testedExposer = testedExposer;
	}

	protected KasperClient client() {
		return cli;
	}

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
		servletContext.addServlet(new ServletHolder(testedExposer),
				"/rootpath/*");

		server.start();
		port = server.getConnectors()[0].getLocalPort();

		cli = new KasperClientBuilder()
				.commandBaseLocation(new URL("http://localhost:" + port + "/rootpath/"))
				.queryBaseLocation(new URL("http://localhost:" + port + "/rootpath/"))
				.create();
	}

	@After
	public void cleanUp() throws Exception {
		server.stop();
	}
}
