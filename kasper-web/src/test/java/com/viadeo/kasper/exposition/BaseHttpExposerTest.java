// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;

import java.net.URL;

public abstract class BaseHttpExposerTest {

    private static final String HTTP_ENDPOINT = "http://127.0.0.1";
    private static final String ROOTPATH = "/rootpath/";

	private Server server;
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
        HttpExposerPlugin exposerPlugin = createExposerPlugin();

        new Platform.Builder()
                .withEventBus(new KasperEventBus())
                .withCommandGateway(new KasperCommandGateway(new KasperCommandBus()))
                .withQueryGateway(new KasperQueryGateway())
                .withConfiguration(ConfigFactory.empty())
                .withMetricRegistry(new MetricRegistry())
                .addPlugin(exposerPlugin)
                .addDomainBundle(getDomainBundle())
                .build();

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(new ServletHolder(exposerPlugin.getHttpExposer()), "/rootpath/*");

        server = new Server(0);
        server.setHandler(servletContext);
        server.start();

        final int port = server.getConnectors()[0].getLocalPort();
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

    protected abstract HttpExposerPlugin createExposerPlugin();

    protected abstract DomainBundle getDomainBundle();
	
	@After
	public void cleanUp() throws Exception {
		server.stop();
	}

}
