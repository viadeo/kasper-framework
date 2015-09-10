// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.viadeo.kasper.core.component.command.spring.KasperCommandConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.KasperEventBusConfiguration;
import com.viadeo.kasper.core.component.query.spring.KasperQueryConfiguration;
import com.viadeo.kasper.core.context.spring.KasperContextConfiguration;
import com.viadeo.kasper.core.id.spring.KasperIDConfiguration;
import com.viadeo.kasper.core.interceptor.authentication.spring.AuthenticationConfiguration;
import com.viadeo.kasper.core.metrics.spring.KasperMetricsConfiguration;
import com.viadeo.kasper.platform.builder.DefaultPlatform;
import com.viadeo.kasper.platform.builder.SpringPlatform;
import com.viadeo.kasper.platform.spring.KasperObjectMapperConfiguration;
import com.viadeo.kasper.platform.spring.KasperPlatformSpringConfiguration;

// TOSO SPRING import com.viadeo.kasper.core.component.event.spring.KasperRabbitMQEventConfiguration;

public final class Platforms {

    private Platforms() {}

    public static SpringPlatform.Builder newSpringPlatformBuilder() {
        return new SpringPlatform.Builder()
                .with(
                          KasperObjectMapperConfiguration.class
                        , KasperContextConfiguration.class
                        , KasperIDConfiguration.class
                        , KasperMetricsConfiguration.class
                        , KasperCommandConfiguration.class
                        , KasperQueryConfiguration.class
                        , AuthenticationConfiguration.class
                        , KasperEventBusConfiguration.class
                        , KasperPlatformSpringConfiguration.class
                );
    }

    public static DefaultPlatform.Builder newDefaultBuilder() {
        return new DefaultPlatform.Builder();
    }

    public static DefaultPlatform.Builder newDefaultBuilder(com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration configuration) {
        return new DefaultPlatform.Builder(configuration);
    }
}
