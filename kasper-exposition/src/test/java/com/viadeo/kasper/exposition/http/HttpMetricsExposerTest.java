// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
