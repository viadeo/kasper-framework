// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.EventListenerDescriptor;
import com.viadeo.kasper.event.Event;

import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class HttpEventExposerPlugin implements HttpExposerPlugin {

    private HttpEventExposer httpEventExposer;

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform,
                           final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {

        final Set<Class<? extends Event>> eventClasses = Sets.newHashSet();
        for (final DomainDescriptor domainDescriptor:domainDescriptors) {
            for (final EventListenerDescriptor eventListenerDescriptor : domainDescriptor.getEventListenerDescriptors()) {
                eventClasses.add(eventListenerDescriptor.getEventClass());
            }
        }

        this.httpEventExposer = new HttpEventExposer(platform.getEventBus(), Lists.newArrayList(eventClasses));
    }

    @Override
    public HttpEventExposer getHttpExposer() {
        checkState(httpEventExposer != null, "The plugin should be initialized.");
        return httpEventExposer;
    }

}
