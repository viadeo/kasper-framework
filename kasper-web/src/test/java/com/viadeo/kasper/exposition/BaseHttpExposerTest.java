// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.NewPlatform;
import com.viadeo.kasper.client.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.cqrs.command.impl.DefaultCommandGateway;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;

import java.net.URL;

public abstract class BaseHttpExposerTest {

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

        new NewPlatform.Builder()
                .withEventBus(new KasperEventBus())
                .withCommandGateway(new DefaultCommandGateway(new KasperCommandBus()))
                .withQueryGateway(new DefaultQueryGateway())
                .withConfiguration(ConfigFactory.empty())
                .addPlugin(exposerPlugin)
                .addDomainBundle(getDomainBundle())
                .build();

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(new ServletHolder(exposerPlugin.getHttpExposer()), "/rootpath/*");

        server = new Server(0);
        server.setHandler(servletContext);
        server.start();

        int port = server.getConnectors()[0].getLocalPort();
        KasperClientBuilder clientBuilder = new KasperClientBuilder();
        clientBuilder
				.commandBaseLocation(new URL("http://127.0.0.1:" + port + "/rootpath/"))
				.queryBaseLocation(new URL("http://127.0.0.1:" + port + "/rootpath/"));
        customize(clientBuilder);
        cli = clientBuilder.create();
	}

    protected void customize(KasperClientBuilder clientBuilder) {

    }

    protected abstract HttpExposerPlugin createExposerPlugin();

    protected abstract DomainBundle getDomainBundle();
	
	@After
	public void cleanUp() throws Exception {
		server.stop();
	}
}
