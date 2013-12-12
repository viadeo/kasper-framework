package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.EventListenerDescriptor;
import com.viadeo.kasper.event.Event;

import java.util.Set;

public class HttpEventExposerPlugin implements HttpExposerPlugin {

    private HttpEventExposer httpEventExposer;

    @Override
    public void initialize(Platform platform, MetricRegistry metricRegistry, DomainDescriptor... domainDescriptors) {
        Set<Class<? extends Event>> eventClasses = Sets.newHashSet();

        for(DomainDescriptor domainDescriptor:domainDescriptors) {
            for (EventListenerDescriptor eventListenerDescriptor : domainDescriptor.getEventListenerDescriptors()) {
                eventClasses.add(eventListenerDescriptor.getEventClass());
            }
        }

        this.httpEventExposer = new HttpEventExposer(platform.getEventBus(), Lists.newArrayList(eventClasses));
    }

    @Override
    public HttpEventExposer getHttpExposer() {
        Preconditions.checkState(httpEventExposer != null, "The plugin should be initialized.");
        return httpEventExposer;
    }
}
