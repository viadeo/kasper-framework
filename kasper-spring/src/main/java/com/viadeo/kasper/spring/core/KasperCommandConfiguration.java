// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.command.DefaultRepositoryManager;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandBus;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperCommandConfiguration {

    /**
     * Command gateway is responsible for dispatching command to the appropriate command handler
     *
     * @param metricRegistry the metric registry
     * @return command gateway
     */
    @Bean
    public KasperCommandGateway commandGateway(final MetricRegistry metricRegistry) {
        UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
        KasperCommandBus commandBus = new KasperCommandBus(metricRegistry);
        commandBus.setUnitOfWorkFactory(uowFactory);
        return new KasperCommandGateway(commandBus);
    }

    @Bean
    public RepositoryManager repositoryManager() {
        return new DefaultRepositoryManager();
    }
}
