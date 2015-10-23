// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.spring;

import com.viadeo.kasper.spring.core.KasperEventBusConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQEventBusConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import( {
        KasperEventBusConfiguration.class,
        RabbitMQEventBusConfiguration.class,
//        SagaConfiguration.class,
        RabbitMQConfiguration.class
})
public class KasperRabbitMQEventConfiguration {

}
