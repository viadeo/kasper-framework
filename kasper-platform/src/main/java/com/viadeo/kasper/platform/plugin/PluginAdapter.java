// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.plugin;

import com.google.common.collect.Lists;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;

import java.util.List;

public abstract class PluginAdapter implements Plugin {

    @Override
    public void initialize(final PlatformContext platform) { }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public <E> List<E> get(final Class<E> clazz) {
        return Lists.newArrayList();
    }

    @Override
    public void onPlatformStarted(final Platform platform) { /* Do nothing */ }

    @Override
    public void onPlatformStopped(final Platform platform) { /* Do nothing */ }

    @Override
    public void onDomainRegistered(final DomainDescriptor domainDescriptor) { /* Do nothing */ }

    @Override
    public void onPluginRegistered(final Plugin plugin) { /* Do nothing */  }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }

}
