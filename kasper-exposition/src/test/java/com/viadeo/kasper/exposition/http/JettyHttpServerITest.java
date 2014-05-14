// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.doc.DocumentationPlugin;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.hamcrest.core.StringContains;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test the integration between the Jetty HTTP server, the config and Kasper components (query/command handlers...)
 */
public class JettyHttpServerITest {

    private static JettyHttpServer server = createServer();

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            server = createServer();
            server.start();
        }

        @Override
        protected void after() {
            server.stop();
        }
    };

    @Test
    public void server_exposes_query_handlers() throws Exception {
        // When
        KasperClient client = new KasperClientBuilder().queryBaseLocation(getServerUri() + "/kasper/query/").create();
        QueryResponse<DummyQueryResult> result = client.query(new DefaultContext(), new DummyQuery(), DummyQueryResult.class);

        // Then
        assertNotNull(result.getResult());
        assertEquals("foo", result.getResult().foo);
    }

    @Test
    public void server_exposes_command_handlers() throws Exception {
        // When
        KasperClient client = new KasperClientBuilder().commandBaseLocation(getServerUri() + "/kasper/command/").create();
        CommandResponse result = client.send(new DefaultContext(), new DummyCommand());

        // Then
        assertEquals(CommandResponse.Status.OK, result.getStatus());
    }

    @Test
    public void server_exposes_event_listeners() throws Exception {
        // When
        KasperClient client = new KasperClientBuilder().eventBaseLocation(getServerUri() + "/kasper/event/").create();
        client.emit(new DefaultContext(), new DummyEvent());

        // Then
    }

    @Test
    public void server_exposes_json_documentation() throws Exception {
        // When
        Client client = Client.create();
        ClientResponse response = client.resource(getServerUri()).path("/kasper/doc/domains").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaders().get("Content-Type").get(0));
        String json = response.getEntity(String.class);
        assertThat(json, StringContains.containsString("foobar"));
    }

    @Test
    public void server_exposes_static_resources_for_the_documentation() throws Exception {
        // When
        Client client = Client.create();
        ClientResponse response = client.resource(getServerUri()).path("/doc/index.htm").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void server_exposes_admin_on_different_port() throws Exception {
        // When
        Client client = Client.create();
        ClientResponse response = client.resource(getAdminServerUri()).get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    private String getServerUri() {
        return "http://localhost:" + server.getPort();
    }

    private String getAdminServerUri() {
        return "http://localhost:" + server.getAdminPort();
    }

    private static JettyHttpServer createServer() {

        ImmutableMap<String, Object> httpConfig = ImmutableMap.<String, Object>builder()
                .put("port", 0)
                .put("adminPort", 0)
                .put("bindHost", "127.0.0.1")
                .put("maxThreads", 140)
                .put("maxIdleTime", "200s")
                .put("acceptQueueSize", 25)
                .put("requestBufferSize", "16KiB")
                .put("requestHeaderBufferSize", "6KiB")
                .put("responseBufferSize", "48KiB")
                .put("responseHeaderBufferSize", "6KiB")
                .put("useForwardedHeaders", true)
                .put("lowResourcesMaxIdleTime", "5s")
                .put("acceptorThreadPriorityOffset", 0)
                .put("shutdownGracePeriod", "2s")
                .put("path.query", "/kasper/query/*")
                .put("path.command", "/kasper/command/*")
                .put("path.event", "/kasper/event/*")
                .put("jmx.enabled", false)
                .put("acceptorThreads", 1)
                .put("minThreads", 5)
                .put("maxBufferCount", 1024)
                .build();

        Config config = ConfigFactory.empty()
                .withFallback(ConfigFactory.parseMap(httpConfig).atPath("runtime.http"))
                .withValue("infrastructure.graphite.port", ConfigValueFactory.fromAnyRef(4400))
                .withValue("infrastructure.graphite.host", ConfigValueFactory.fromAnyRef("localhost"))
                .withValue("kasper.workers.threadPool.size", ConfigValueFactory.fromAnyRef(10))
                .withValue("kasper.boot.scanPrefixes", ConfigValueFactory.fromAnyRef(Arrays.asList("com.viadeo.platform.http")))
                .withValue("runtime.spring.domains", ConfigValueFactory.fromAnyRef(Arrays.asList("com.viadeo.platform.http.JettyHttpServerTest.FoobarConfiguration")));

        HttpCommandExposerPlugin httpCommandExposerPlugin = new HttpCommandExposerPlugin();
        HttpQueryExposerPlugin httpQueryExposerPlugin = new HttpQueryExposerPlugin();
        HttpEventExposerPlugin httpEventExposerPlugin = new HttpEventExposerPlugin();
        DocumentationPlugin documentationPlugin = new DocumentationPlugin();

        new Platform.Builder(new KasperPlatformConfiguration())
                .withEventBus(new KasperEventBus(KasperEventBus.Policy.ASYNCHRONOUS))
                .withConfiguration(config)
                .addPlugin(httpCommandExposerPlugin)
                .addPlugin(httpQueryExposerPlugin)
                .addPlugin(httpEventExposerPlugin)
                .addPlugin(documentationPlugin)
                .addDomainBundle(
                        new DomainBundle.Builder(new Foobar())
                                .with(new DummyCommandHandler())
                                .with(new DummyQueryService())
                                .with(new DummyEventListener())
                                .build()
                )
                .build();

        DefaultResourceConfig resourceConfig = new DefaultResourceConfig();
        resourceConfig.getSingletons().add(documentationPlugin.getKasperDocResource());

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(Resources.getResource("META-INF/resources/doc").toExternalForm());
        resourceHandler.setWelcomeFiles(new String[]{"index.htm"});

        return new JettyHttpServer(
                new JettyConfiguration(config.getConfig("runtime.http"))
                , httpQueryExposerPlugin.getHttpExposer()
                , httpCommandExposerPlugin.getHttpExposer()
                , httpEventExposerPlugin.getHttpExposer()
                , resourceConfig
                , new HealthCheckRegistry()
                , KasperMetrics.getMetricRegistry()
                , ImmutableMap.<String, ResourceHandler>builder().put("/doc", resourceHandler).build()
        );
    }


    // Dummy domain definition

    @XKasperDomain(prefix = "foo", label = "foobar", description = "foobar foo foo")
    public static class Foobar implements Domain {
    }

    public static class DummyQuery implements Query {
        public DummyQuery() {
        }
    }

    public static class DummyQueryResult implements QueryResult {
        public String foo = "foo";
    }

    public static class DummyCommand implements Command {
        public DummyCommand() {
        }
    }

    public static class DummyEvent extends Event {
        public DummyEvent() {
        }
    }

    @XKasperQueryHandler(domain = Foobar.class)
    public static class DummyQueryService extends QueryHandler<DummyQuery, DummyQueryResult> {
        @Override
        public QueryResponse<DummyQueryResult> retrieve(DummyQuery query) throws Exception {
            return QueryResponse.of(new DummyQueryResult());
        }
    }

    @XKasperCommandHandler(domain = Foobar.class)
    public static class DummyCommandHandler extends CommandHandler<DummyCommand> {

        @Override
        public CommandResponse handle(DummyCommand command) throws Exception {
            return CommandResponse.ok();
        }
    }

    @XKasperCommandHandler(domain = Foobar.class)
    public static class DummyEventListener extends EventListener<DummyEvent> {

        @Override
        public void handle(final DummyEvent event){ }
    }
}

