// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.platform;

import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.builder.BuilderContext;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;

import java.util.List;

public class SpringBuilderContext extends BuilderContext {

    public SpringBuilderContext(final KasperPlatformConfiguration platformConfiguration,
                                final List<ExtraComponent> extraComponents
    ) {
        super(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                extraComponents
        );
    }

}
