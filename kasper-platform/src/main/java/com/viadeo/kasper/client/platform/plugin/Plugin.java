package com.viadeo.kasper.client.platform.plugin;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;

/**
 * The Plugin interface represents an extension way to the platform useful to add new functionality like : documentation,
 * exposition, etc...
 *
 * Note that the plugin will be initialized after the components is wired during the construction of the platform.
 */
public interface Plugin {

    /**
     * Initialize the plugin
     *
     * @param platform the newly created platform
     * @param metricRegistry the metric registry used by the platform
     * @param domainDescriptors the domain descriptors of each registered domain bundle on the platform
     */
    void initialize(final Platform platform, final MetricRegistry metricRegistry, final DomainDescriptor... domainDescriptors);

}
