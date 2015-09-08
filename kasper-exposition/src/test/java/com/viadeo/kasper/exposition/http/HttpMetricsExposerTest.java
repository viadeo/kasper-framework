// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.Meter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class HttpMetricsExposerTest extends BaseHttpExposerTest {

    @Override
    protected HttpMetricsExposerPlugin createExposerPlugin() {
        return new HttpMetricsExposerPlugin();
    }

    @Override
    protected DomainBundle getDomainBundle() {
        return null;
    }

    @Test
    public void testMetrics() throws IOException {
        // Given
        final Client client = Client.create();
        final WebResource webResource = client.resource(url() + "");

        final Meter meter = KasperMetrics.getMetricRegistry().meter("com.viadeo.kasper.test");
        meter.mark();

        final String expectedOutput =
                "{\"counters\":{\"com.viadeo.kasper.core.component.event.eventbus.KasperProcessorDownLatch.running\":"
              + "{\"count\":0},\"com.viadeo.kasper.core.component.event.eventbus.KasperEventBus.running\":{\"count\":0}},"
              + "\"gauges\":{},\"histograms\":{},\"meters\":{\""
              + "com.viadeo.kasper.core.component.event.eventbus.KasperProcessorDownLatch.submitted\":"
              + "{\"count\":0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"units\":\"events/second\"},"
              + "\"com.viadeo.kasper.core.component.event.eventbus.KasperEventBus.submitted\":{\"count\":0,\"m15_rate\":0.0,\"m1_rate"
              + "\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"units\":\"events/second\"},\"com.viadeo.kasper.test\":{\"count\":1,\"m15_rate\""
              + ":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"units\":\"events/second\"},"
              + "\"com.viadeo.kasper.core.component.event.eventbus.KasperEventBus.completed\":"
              + "{\"count\":0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"units\":\"events/second\"},"
              + "\"com.viadeo.kasper.core.component.event.eventbus.KasperProcessorDownLatch.completed\":"
              + "{\"count\":0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"units\":\"events/second\"}},"
              + "\"timers\":{\"com.viadeo.kasper.core.component.event.eventbus.KasperEventBus.duration\":"
              + "{\"count\":0,\"max\":0.0,\"mean\":0.0,\"min\":0.0,\"p50\":0.0,\"p75\":0.0,\"p95\":0.0,\"p98\":0.0,\"p99\":0.0,"
              + "\"p999\":0.0,\"stddev\":0.0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,\"duration_units\":"
              + "\"milliseconds\",\"rate_units\":\"calls/second\"},"
              + "\"com.viadeo.kasper.core.component.event.eventbus.KasperProcessorDownLatch.duration\":"
              + "{\"count\":0,\"max\":0.0,\"mean\":0.0,\"min\":0.0,\"p50\":0.0,\"p75\":0.0,\"p95\":0.0,\"p98\":0.0,"
              + "\"p99\":0.0,\"p999\":0.0,\"stddev\":0.0,\"m15_rate\":0.0,\"m1_rate\":0.0,\"m5_rate\":0.0,\"mean_rate\":0,"
              + "\"duration_units\":\"milliseconds\",\"rate_units\":\"calls/second\"}}}";

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
