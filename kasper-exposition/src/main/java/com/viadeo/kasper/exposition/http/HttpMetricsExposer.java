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

import com.codahale.metrics.*;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpMetricsExposer extends HttpServlet {

    private static final long serialVersionUID = 8444284924203895624L;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMetricsExposer.class);

    private static final class KasperMetricsFilter implements MetricFilter {
        private final String prefix = KasperMetrics.name("com.viadeo.kasper");
        @Override
        public boolean matches(final String name, final Metric metric) {
            return name.startsWith(prefix);
        }
    }

    @VisibleForTesting
    static final class KasperMetricsOutput {
        public final Map<String, Counter> counters = Maps.newHashMap();
        public final Map<String, Gauge> gauges = Maps.newHashMap();
        public final Map<String, Histogram> histograms = Maps.newHashMap();
        public final Map<String, Meter> meters = Maps.newHashMap();
        public final Map<String, Timer> timers = Maps.newHashMap();

        KasperMetricsOutput() { }
    }

    private static final TimeUnit DEFAULT_RATE_UNIT = TimeUnit.SECONDS;
    private static final TimeUnit DEFAULT_DURATION_UNIT = TimeUnit.MILLISECONDS;

    private static final KasperMetricsFilter kasperMetricsFilter = new KasperMetricsFilter();

    private final MetricRegistry metrics;
    private final ObjectMapper mapper;
    private final TimeUnit rateUnit;
    private final TimeUnit durationUnit;

    // ------------------------------------------------------------------------

    public HttpMetricsExposer(final MetricRegistry metrics, final ObjectMapper mapper) {
        this(metrics, mapper, DEFAULT_RATE_UNIT, DEFAULT_DURATION_UNIT);

    }

    public HttpMetricsExposer(final MetricRegistry metrics, final ObjectMapper mapper, final TimeUnit rateUnit, final TimeUnit durationUnit) {
        this.mapper = checkNotNull(mapper);
        this.rateUnit = checkNotNull(rateUnit);
        this.durationUnit = checkNotNull(durationUnit);
        this.metrics = checkNotNull(metrics);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("\n=============== Exposing metrics resource ===============\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        final KasperMetricsOutput output = new KasperMetricsOutput();

        /* Counters */
        final Map<String, Counter> counters = metrics.getCounters(kasperMetricsFilter);
        for (final Map.Entry<String, Counter> counter : counters.entrySet()) {
            output.counters.put(counter.getKey(), counter.getValue());
        }

        /* Gauges */
        final Map<String, Gauge> gauges = metrics.getGauges(kasperMetricsFilter);
        for (final Map.Entry<String, Gauge> gauge : gauges.entrySet()) {
            output.gauges.put(gauge.getKey(), gauge.getValue());
        }

        /* Histograms */
        final Map<String, Histogram> histograms = metrics.getHistograms(kasperMetricsFilter);
        for (final Map.Entry<String, Histogram> histogram : histograms.entrySet()) {
            output.histograms.put(histogram.getKey(), histogram.getValue());
        }

        /* Meters */
        final Map<String, Meter> meters = metrics.getMeters(kasperMetricsFilter);
        for (final Map.Entry<String, Meter> meter : meters.entrySet()) {
            output.meters.put(meter.getKey(), meter.getValue());
        }

        /* Timers */
        final Map<String, Timer> timers = metrics.getTimers(kasperMetricsFilter);
        for (final Map.Entry<String, Timer> timer : timers.entrySet()) {
            output.timers.put(timer.getKey(), timer.getValue());
        }

        /* Write JSON */
        mapper.enable(MapperFeature.AUTO_DETECT_FIELDS);
        mapper.registerModule(new MetricsModule(this.rateUnit, this.durationUnit, false));
        final ObjectWriter writer = mapper.writer();
        final JsonGenerator generator = writer.getFactory().createGenerator(resp.getOutputStream());
        resp.setContentType(MediaType.APPLICATION_JSON + "; charset=utf-8");
        resp.setStatus(Response.Status.OK.getStatusCode());
        writer.writeValue(generator, output);

        /*
         * must be last call to ensure that everything is sent to the client
         * (even if an error occurred)
         */
        try {
            resp.flushBuffer();
        } catch (final IOException e) {
            LOGGER.warn("Error when trying to flush output buffer", e);
        }
    }

}
