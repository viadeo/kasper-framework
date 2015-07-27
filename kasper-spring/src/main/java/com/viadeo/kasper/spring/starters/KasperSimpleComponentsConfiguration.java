// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.starters;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.command.KasperCommandBus;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperSimpleComponentsConfiguration {

    @Bean
    public KasperEventBus kasperEventBus() {
        return new KasperEventBus();
    }

    @Bean
    public KasperCommandBus kasperCommandBus() {
        return new KasperCommandBus();
    }

    @Bean
    public KasperCommandGateway kasperCommandGateway(final KasperCommandBus commandBus, final MetricRegistry metricRegistry) {
        return new KasperCommandGateway(commandBus, metricRegistry);
    }

    @Bean
    public KasperQueryGateway kasperQueryGateway() {
        return new KasperQueryGateway();
    }

    @Bean
    public HealthCheck voidHealthCheck() {
        return new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        };
    }

}
