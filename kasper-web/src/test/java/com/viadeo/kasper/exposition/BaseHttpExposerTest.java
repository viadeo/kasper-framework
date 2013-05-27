package com.viadeo.kasper.exposition;

import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.platform.web.KasperPlatformBootListener;

public abstract class BaseHttpExposerTest<T extends HttpExposer> {
	private Server server;
	private int port;
	private KasperClient cli;

	protected BaseHttpExposerTest() {
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
		XmlWebApplicationContext ctx = new XmlWebApplicationContext();
		ctx.setConfigLocation("classpath:spring/kasper/spring-kasper-platform.xml");
		ctx.refresh();
		// ugly again :/ hard to test, hard to register a commandhandler for
		// testing purpose etc...
//		servletContext.setInitParameter("contextConfigLocation",
//				"classpath:spring/kasper/spring-kasper-platform.xml");
		servletContext.addEventListener(new ContextLoaderListener(ctx));
		servletContext.addEventListener(new KasperPlatformBootListener());
		servletContext.addServlet(new ServletHolder(createExposer(ctx)),
				"/rootpath/*");

		server.start();
		port = server.getConnectors()[0].getLocalPort();

		cli = new KasperClientBuilder()
				.commandBaseLocation(new URL("http://localhost:" + port + "/rootpath/"))
				.queryBaseLocation(new URL("http://localhost:" + port + "/rootpath/"))
				.create();
	}

	protected abstract T createExposer(ApplicationContext ctx);
	
	@After
	public void cleanUp() throws Exception {
		server.stop();
	}
}
