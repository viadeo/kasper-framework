// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.EventListenerDescriptor;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.util.Set;

public class HttpEventExposerPlugin extends HttpExposerPlugin<HttpEventExposer> {

    public HttpEventExposerPlugin() {
        this(ObjectMapperProvider.INSTANCE.mapper());
    }

    // ------------------------------------------------------------------------

    public HttpEventExposerPlugin(final ObjectMapper objectMapper) {
        this(new HttpContextDeserializer(), objectMapper);
    }

    public HttpEventExposerPlugin(final HttpContextDeserializer httpContextDeserializer,
                                  final ObjectMapper objectMapper) {
        super(httpContextDeserializer, objectMapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform,
                           final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {

        final Set<ExposureDescriptor<Event,EventListener>> exposureDescriptors = Sets.newHashSet();
        for (final DomainDescriptor domainDescriptor:domainDescriptors) {
            for (final EventListenerDescriptor eventListenerDescriptor : domainDescriptor.getEventListenerDescriptors()) {
                exposureDescriptors.add(new ExposureDescriptor<>(eventListenerDescriptor.getEventClass(), eventListenerDescriptor.getReferenceClass()));
            }
        }

        initialize(
            new HttpEventExposer(
                platform.getEventBus(),
                platform.getMeta(),
                Lists.newArrayList(exposureDescriptors),
                getContextDeserializer(),
                getMapper()
            )
        );
    }

}
