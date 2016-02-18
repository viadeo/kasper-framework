// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.FormatAdapter;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.id.TestFormats;
import com.viadeo.kasper.platform.builder.DefaultPlatform;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.platform.configuration.PlatformConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseHttpExposerTest {

    private static final String HTTP_ENDPOINT = "http://127.0.0.1";
    private static final String ROOT_PATH = "/rootpath/";

	private Server server;
	private KasperClient cli;
    private Client client;
    private int port;
    protected HttpExposurePlugin httpExposurePlugin;

    // ------------------------------------------------------------------------

	protected BaseHttpExposerTest() { }

    // ------------------------------------------------------------------------

	protected KasperClient client() {
		return cli;
	}

    protected Client httpClient() {
        return client;
    }

    protected String url() {
        return HTTP_ENDPOINT + ":" + port + ROOT_PATH;
    }

    // ------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
        httpExposurePlugin = new HttpExposurePlugin() {
            @Override
            protected void initServer(PlatformContext context) { }
        };

        platformBuilder(new KasperPlatformConfiguration(), getDomainBundle()).build();

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(new ServletHolder(getHttpExposer()), ROOT_PATH + "*");

        server = new Server(0);
        server.setHandler(servletContext);
        server.start();

        port = server.getConnectors()[0].getLocalPort();

        final DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(ObjectMapperProvider.INSTANCE.mapper()));

        client = Client.create(cfg);

        cli = clientBuilder().create();
	}

    protected KasperClientBuilder clientBuilder() throws MalformedURLException {
        final URL fullPath = new URL(url());
        return new KasperClientBuilder()
                .client(client)
                .commandBaseLocation(fullPath)
                .eventBaseLocation(fullPath)
                .queryBaseLocation(fullPath);
    }

    protected DefaultPlatform.Builder platformBuilder(final PlatformConfiguration platformConfiguration, final DomainBundle domainBundle) {
        checkNotNull(domainBundle);
        return new DefaultPlatform.Builder(platformConfiguration)
                .addPlugin(httpExposurePlugin)
                .addDomainBundle(domainBundle);
    }

    protected abstract HttpExposer getHttpExposer();

    protected abstract DomainBundle getDomainBundle();
	
	@After
	public void cleanUp() throws Exception {
        if (null != server) {
            server.stop();
        }
	}

    protected static String getContextName() {
        return "full";
    }

    protected static Context getFullContext() {
        final IDBuilder idBuilder = new SimpleIDBuilder(TestFormats.UUID, TestFormats.ID);
        return Contexts.builder()
                .withSessionCorrelationId(UUID.randomUUID().toString())
                .withFunnelCorrelationId(UUID.randomUUID().toString())
                .withFunnelName("MyFunnel")
                .withFunnelVersion("case_1")
                .withUserID(idBuilder.build("urn:viadeo:member:id:42"))
                .withUserLang("us")
                .withUserCountry("US")
                .withApplicationId("TEST")
                .withSecurityToken(UUID.randomUUID().toString())
                .withIpAddress("127.0.0.1")
                .build();
    }

    private static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) new Integer(identifier);
        }
    };
}
