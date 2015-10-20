// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.platform;

import com.viadeo.kasper.platform.builder.DefaultPlatform;
import com.viadeo.kasper.spring.core.*;

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
                );
    }

    public static DefaultPlatform.Builder newDefaultBuilder() {
        return new DefaultPlatform.Builder();
    }

    public static DefaultPlatform.Builder newDefaultBuilder(com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration configuration) {
        return new DefaultPlatform.Builder(configuration);
    }
}
