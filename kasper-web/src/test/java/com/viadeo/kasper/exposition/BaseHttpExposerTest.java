// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
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

        buildPlatform(new KasperPlatformConfiguration(), exposerPlugin, getDomainBundle());

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

    protected void buildPlatform(PlatformConfiguration platformConfiguration, HttpExposerPlugin httpExposerPlugin, DomainBundle domainBundle){
        new Platform.Builder(platformConfiguration)
                .addPlugin(httpExposerPlugin)
                .addDomainBundle(domainBundle)
                .build();
    }

    protected abstract HttpExposerPlugin createExposerPlugin();

    protected abstract DomainBundle getDomainBundle();
	
	@After
	public void cleanUp() throws Exception {
		server.stop();
	}

}
