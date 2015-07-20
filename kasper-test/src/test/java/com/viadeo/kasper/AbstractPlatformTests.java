// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;

import java.util.List;

public abstract class AbstractPlatformTests {

    private Platform platform;

    protected Platform getPlatform() {
        if(platform == null){
            final Platform.Builder platformBuilder = new Platform.Builder(new KasperPlatformConfiguration());

            for (final DomainBundle domainBundle: Preconditions.checkNotNull(getBundles())) {
                platformBuilder.addDomainBundle(domainBundle);
            }

            this.platform = platformBuilder.build();
        }
        return this.platform;
    }

    protected Context newContext() {
        return Contexts.empty();
    }

    public abstract List<DomainBundle> getBundles();

}
