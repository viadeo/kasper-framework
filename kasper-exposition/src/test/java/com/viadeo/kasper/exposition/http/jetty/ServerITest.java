// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.client.KasperClient;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.doc.DocumentationPlugin;
import com.viadeo.kasper.exposition.http.HttpExposurePlugin;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.builder.DefaultPlatform;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import org.hamcrest.core.StringContains;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
* Test the integration between the Jetty HTTP server, the config and Kasper components (query/command handlers...)
*/
public class ServerITest {

    private static Platform platform = createPlatform();

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            platform.start();
        }

        @Override
        protected void after() {
            platform.stop();
        }
    };

    @Test
    public void server_exposes_query_handlers() throws Exception {
        // Given
        KasperClient client = new KasperClientBuilder().queryBaseLocation(getServerUri() + "/kasper/query/").create();

        // When
        QueryResponse<DummyQueryResult> result = client.query(Contexts.empty(), new DummyQuery(), DummyQueryResult.class);

        // Then
        assertNotNull(result.getResult());
        assertEquals("foo", result.getResult().foo);
    }

    @Test
    public void server_exposes_command_handlers() throws Exception {
        // Given
        KasperClient client = new KasperClientBuilder().commandBaseLocation(getServerUri() + "/kasper/command/").create();

        // When
        CommandResponse result = client.send(Contexts.empty(), new DummyCommand());

        // Then
        assertEquals(CommandResponse.Status.OK, result.getStatus());
    }

    @Test
    public void server_exposes_event_listeners() throws Exception {
        // Given
        KasperClient client = new KasperClientBuilder().eventBaseLocation(getServerUri() + "/kasper/event/").create();

        // When
        client.emit(Contexts.empty(), new DummyEvent());

        // Then
    }

    //    @Ignore
    @Test
    public void server_exposes_json_documentation() throws Exception {
        // Given
        Client client = Client.create();

        // When
        ClientResponse response = client.resource(getServerUri()).path("/kasper/doc/domains").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaders().get("Content-Type").get(0));
        String json = response.getEntity(String.class);
        assertThat(json, StringContains.containsString("foobar"));
    }

    @Test
    public void server_exposes_static_resources_for_the_documentation() throws Exception {
        // Given
        Client client = Client.create();

        // When
        ClientResponse response = client.resource(getServerUri()).path("/doc/index.html").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void server_exposes_static_resources_for_the_documentation_at_the_root_level() throws Exception {
        // Given
        Client client = Client.create();

        // When
        ClientResponse response = client.resource(getServerUri()).path("/").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void server_exposes_admin_on_different_port() throws Exception {
        // Given
        Client client = Client.create();

        // When
        ClientResponse response = client.resource(getAdminServerUri()).get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    private String getServerUri() {
        return "http://localhost:8080";
    }

    private String getAdminServerUri() {
        return "http://localhost:9090";
    }

    private static Platform createPlatform() {
        ImmutableMap<String, Object> httpConfig = ImmutableMap.<String, Object>builder()
                .put("port", 8080)
                .put("adminPort", 9090)
                .put("bindHost", "127.0.0.1")
                .put("maxThreads", 140)
                .put("maxIdleTime", "200s")
                .put("acceptQueueSize", 25)
                .put("maxQueued", 0)
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

        HttpExposurePlugin httpExposurePlugin = new HttpExposurePlugin();
        DocumentationPlugin documentationPlugin = new DocumentationPlugin();
        MetricRegistry metricRegistry = new MetricRegistry();

        return new DefaultPlatform.Builder(new KasperPlatformConfiguration(metricRegistry))
                .withEventBus(new KasperEventBus(metricRegistry))
                .withConfiguration(config)
                .addPlugin(httpExposurePlugin)
                .addPlugin(documentationPlugin)
                .addDomainBundle(
                        new DomainBundle.Builder(new Foobar())
                                .with(new DummyCommandHandler())
                                .with(new DummyQueryService())
                                .with(new DummyEventListener())
                                .build()
                )
                .build();
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

    public static class DummyEvent implements Event {
        public DummyEvent() {
        }
    }

    @XKasperQueryHandler(domain = Foobar.class)
    public static class DummyQueryService extends AutowiredQueryHandler<DummyQuery, DummyQueryResult> {
        @Override
        public QueryResponse<DummyQueryResult> handle(DummyQuery query) {
            return QueryResponse.of(new DummyQueryResult());
        }
    }

    @XKasperCommandHandler(domain = Foobar.class)
    public static class DummyCommandHandler extends AutowiredCommandHandler<DummyCommand> {

        @Override
        public CommandResponse handle(DummyCommand command) {
            return CommandResponse.ok();
        }
    }

    @XKasperCommandHandler(domain = Foobar.class)
    public static class DummyEventListener extends AutowiredEventListener<DummyEvent> {

        @Override
        public EventResponse handle(final Context context, final DummyEvent event) {
            return EventResponse.success();
        }
    }
}
