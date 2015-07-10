// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.starters;

import com.codahale.metrics.health.HealthCheck;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandBus;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
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
    public KasperCommandGateway kasperCommandGateway(final KasperCommandBus commandBus) {
        return new KasperCommandGateway(commandBus);
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
