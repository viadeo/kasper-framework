// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SagaConfiguration.class)
public class KasperEventBusConfiguration {

    @Bean
    public KasperEventBus kasperEventBus(final MetricRegistry metricRegistry) {
        return new KasperEventBus(metricRegistry);
    }

}
