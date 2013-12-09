package com.viadeo.kasper.client.platform;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;

public interface Plugin {

    void initialize(final Platform platform, final MetricRegistry metricRegistry, final DomainDescriptor... domainDescriptors);

}
