// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;

import java.util.List;

public abstract class AbstractPlatformTests {

    private Platform platform;

    protected Platform getPlatform() {
        if(platform == null){
            final Platform.Builder platformBuilder = new Platform.Builder()
                    .withConfiguration(ConfigFactory.empty())
                    .withEventBus(new KasperEventBus())
                    .withCommandGateway(new KasperCommandGateway(new KasperCommandBus()))
                    .withQueryGateway(new KasperQueryGateway())
                    .withMetricRegistry(new MetricRegistry());

            for (final DomainBundle domainBundle: Preconditions.checkNotNull(getBundles())) {
                platformBuilder.addDomainBundle(domainBundle);
            }

            this.platform = platformBuilder.build();
        }
        return this.platform;
    }

    protected Context newContext() {
        return new DefaultContextBuilder().build();
    }

    public abstract List<DomainBundle> getBundles();

}
