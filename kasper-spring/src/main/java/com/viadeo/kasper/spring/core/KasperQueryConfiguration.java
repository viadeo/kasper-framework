// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperQueryConfiguration {

    /**
     * Query gateway is responsible for dispatching queries to the appropriate query handler
     *
     * @param metricRegistry the metric registry
     * @return query gateway
     */
    @Bean
    public KasperQueryGateway queryGateway(final MetricRegistry metricRegistry) {
        return new KasperQueryGateway(metricRegistry);
    }
}
