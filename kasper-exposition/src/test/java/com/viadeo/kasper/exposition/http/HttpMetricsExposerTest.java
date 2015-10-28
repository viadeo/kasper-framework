// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class HttpMetricsExposerTest {

    private static final String HTTP_ENDPOINT = "http://127.0.0.1";
    private static final String ROOT_PATH = "/rootpath/";

    private Server server;
    private Client client;
    private int port;

    @Before
    public void setUp() throws Exception {
        KasperMetrics.setMetricRegistry(new MetricRegistry());

        final HttpMetricsExposer httpMetricsExposer = new HttpMetricsExposer(
                KasperMetrics.getMetricRegistry(),
                ObjectMapperProvider.INSTANCE.mapper()
        );

        final ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");
        servletContext.addServlet(new ServletHolder(httpMetricsExposer), ROOT_PATH + "*");

        server = new Server(0);
        server.setHandler(servletContext);
        server.start();

        port = server.getConnectors()[0].getLocalPort();

        final DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getSingletons().add(new JacksonJsonProvider(ObjectMapperProvider.INSTANCE.mapper()));

        client = Client.create(cfg);
    }

    @After
    public void cleanUp() throws Exception {
        server.stop();
    }

    protected String url() {
        return HTTP_ENDPOINT + ":" + port + ROOT_PATH;
    }

    @Test
    public void testMetrics() throws IOException {
        // Given
        final WebResource webResource = client.resource(url() + "");

        final Meter meter = KasperMetrics.getMetricRegistry().meter("com.viadeo.kasper.test");
        meter.mark();

        final String expectedOutput = "{\"counters\":{},\"gauges\":{},\"histograms\":{},\"meters\":{\"com.viadeo.kasper.test\":{\"count\":1,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"units\":\"events/second\"}},\"timers\":{}}";

        // When
        final ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());

        final InputStream inputStream = response.getEntityInputStream();
        final StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));

        String output = writer.toString();
        output = output.replaceAll("\"mean_rate\":[0-9\\.]+,\"", "\"mean_rate\":0,\"");

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode expectedOutputJson = mapper.readTree(expectedOutput);
        final JsonNode outputJson = mapper.readTree(output);

        assertEquals(expectedOutputJson, outputJson);
    }

}
