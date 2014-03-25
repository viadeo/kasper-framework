// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpMetricsExposerPlugin extends HttpExposerPlugin<HttpMetricsExposer> {

    private static final TimeUnit DEFAULT_RATE_UNIT = TimeUnit.SECONDS;
    private static final TimeUnit DEFAULT_DURATION_UNIT = TimeUnit.MILLISECONDS;

    private final TimeUnit rateUnit;
    private final TimeUnit durationUnit;

    // ------------------------------------------------------------------------

    public HttpMetricsExposerPlugin() {
        this(ObjectMapperProvider.INSTANCE.mapper(), DEFAULT_RATE_UNIT, DEFAULT_DURATION_UNIT);
    }

    public HttpMetricsExposerPlugin(final TimeUnit rateUnit, final TimeUnit durationUnit) {
        this(ObjectMapperProvider.INSTANCE.mapper(), rateUnit, durationUnit);
    }

    public HttpMetricsExposerPlugin(final ObjectMapper objectMapper) {
        this(new HttpContextDeserializer(), objectMapper, DEFAULT_RATE_UNIT, DEFAULT_DURATION_UNIT);
    }

    public HttpMetricsExposerPlugin(final ObjectMapper objectMapper,
                                    final TimeUnit rateUnit,
                                    final TimeUnit durationUnit) {
        this(new HttpContextDeserializer(), objectMapper, rateUnit, durationUnit);
    }

    public HttpMetricsExposerPlugin(final HttpContextDeserializer httpContextDeserializer,
                                    final ObjectMapper objectMapper) {
        this(httpContextDeserializer, objectMapper, DEFAULT_RATE_UNIT, DEFAULT_DURATION_UNIT);
    }

    public HttpMetricsExposerPlugin(final HttpContextDeserializer httpContextDeserializer,
                                    final ObjectMapper objectMapper,
                                    final TimeUnit rateUnit,
                                    final TimeUnit durationUnit) {
        super(httpContextDeserializer, objectMapper);
        this.rateUnit = checkNotNull(rateUnit);
        this.durationUnit = checkNotNull(durationUnit);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform, final MetricRegistry metricRegistry, final DomainDescriptor... domainDescriptors) {
        initialize(
                new HttpMetricsExposer(
                        metricRegistry,
                        this.getMapper(),
                        rateUnit,
                        durationUnit
                )
        );
    }

}
