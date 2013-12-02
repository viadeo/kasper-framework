package com.viadeo.kasper.client.platform;

import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;

public interface Plugin {

    void initialize(NewPlatform platform, DomainDescriptor... domainDescriptors);

}
