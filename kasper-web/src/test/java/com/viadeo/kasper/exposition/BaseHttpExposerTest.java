// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.configuration.DefaultPlatformSpringConfiguration;
import com.viadeo.kasper.web.KasperPlatformSpringBootListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.net.URL;

public abstract class BaseHttpExposerTest<T extends HttpExposer> {

    private static final String HTTP_ENDPOINT = "http://127.0.0.1";
    private static final String ROOTPATH = "/rootpath/";

	private Server server;
	private int port;
	private KasperClient cli;

    // ------------------------------------------------------------------------

	protected BaseHttpExposerTest() { }

    // ------------------------------------------------------------------------

	protected KasperClient client() {
		return cli;
	}

    // ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		server = new Server(0);

		final ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.setContextPath("/");
		server.setHandler(servletContext);

        final AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(DefaultPlatformSpringConfiguration.class);
        ctx.refresh();

		servletContext.addEventListener(new ContextLoaderListener(ctx));
		servletContext.addEventListener(new KasperPlatformSpringBootListener());
		servletContext.addServlet(new ServletHolder(createExposer(ctx)), "/rootpath/*");

		server.start();
		port = server.getConnectors()[0].getLocalPort();

        final URL fullPath = new URL(HTTP_ENDPOINT + ":" + port + ROOTPATH);

        KasperClientBuilder clientBuilder = new KasperClientBuilder();
		clientBuilder
				.commandBaseLocation(fullPath)
                .eventBaseLocation(fullPath)
				.queryBaseLocation(fullPath);
        customize(clientBuilder);

        cli = clientBuilder.create();
	}

    protected void customize(final KasperClientBuilder clientBuilder) {
        /* FIXME: wtf ? */
    }

	protected abstract T createExposer(final ApplicationContext ctx);
	
	@After
	public void cleanUp() throws Exception {
		server.stop();
	}

}
