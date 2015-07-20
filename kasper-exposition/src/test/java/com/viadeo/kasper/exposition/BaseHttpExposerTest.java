// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.bundle.DomainBundle;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.exposition.http.HttpExposerPlugin;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;

import java.net.URL;
import java.util.UUID;

public abstract class BaseHttpExposerTest {

    private static final String HTTP_ENDPOINT = "http://127.0.0.1";
    private static final String ROOTPATH = "/rootpath/";

	private Server server;
	private KasperClient cli;
    private Client client;
    private Platform platform;
    private int port;

    // ------------------------------------------------------------------------

	protected BaseHttpExposerTest() { }

    // ------------------------------------------------------------------------

	protected KasperClient client() {
		return cli;
	}

    protected Client httpClient() {
        return client;
    }

    protected int port() {
        return port;
    }

    protected String url() {
        return HTTP_ENDPOINT + ":" + port + ROOTPATH;
    }

    // ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
        final HttpExposerPlugin exposerPlugin = createExposerPlugin();

        buildPlatform(new KasperPlatformConfiguration(), exposerPlugin, getDomainBundle());

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(new ServletHolder(exposerPlugin.getHttpExposer()), "/rootpath/*");

        server = new Server(0);
        server.setHandler(servletContext);
        server.start();

        port = server.getConnectors()[0].getLocalPort();

        final DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(ObjectMapperProvider.INSTANCE.mapper()));
        client = Client.create(cfg);

        final URL fullPath = new URL(url());

        final KasperClientBuilder clientBuilder = new KasperClientBuilder()
                .client(client)
                .commandBaseLocation(fullPath)
                .eventBaseLocation(fullPath)
                .queryBaseLocation(fullPath);
        
        customize(clientBuilder);

        cli = clientBuilder.create();
	}

    protected void customize(final KasperClientBuilder clientBuilder) {
        /* FIXME: wtf ? */
    }

    protected void buildPlatform(final PlatformConfiguration platformConfiguration,
                                 final HttpExposerPlugin httpExposerPlugin,
                                 final DomainBundle domainBundle) {
        final Platform.Builder builder = new Platform.Builder(platformConfiguration).addPlugin(httpExposerPlugin);
        if (null != domainBundle) {
            builder.addDomainBundle(domainBundle);
        }
        platform = builder.build();
    }

    protected abstract HttpExposerPlugin createExposerPlugin();

    protected abstract DomainBundle getDomainBundle();
	
	@After
	public void cleanUp() throws Exception {
		server.stop();
	}

    protected static String getContextName() {
        return "full";
    }

    protected static Context getFullContext() {
        return Contexts.builder()
                .withSessionCorrelationId(UUID.randomUUID().toString())
                .withFunnelCorrelationId(UUID.randomUUID().toString())
                .withFunnelName("MyFunnel")
                .withFunnelVersion("case_1")
                .withUserId("42")
                .withUserLang("us")
                .withUserCountry("US")
                .withApplicationId("TEST")
                .withSecurityToken(UUID.randomUUID().toString())
                .withIpAddress("127.0.0.1")
                .build();
    }
}
