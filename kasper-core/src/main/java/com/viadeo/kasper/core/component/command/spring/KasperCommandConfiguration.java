// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.spring;

import com.viadeo.kasper.core.component.command.DefaultRepositoryManager;
import com.viadeo.kasper.core.component.command.KasperCommandBus;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
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
     * @return command gateway
     */
    @Bean
    public CommandGateway commandGateway() {
        UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
        KasperCommandBus commandBus = new KasperCommandBus();
        commandBus.setUnitOfWorkFactory(uowFactory);
        return new KasperCommandGateway(commandBus);
    }

    @Bean
    public RepositoryManager repositoryManager() {
        return new DefaultRepositoryManager();
    }
}
