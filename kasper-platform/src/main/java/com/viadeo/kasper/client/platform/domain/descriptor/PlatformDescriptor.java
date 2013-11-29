package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class PlatformDescriptor {

    private final ImmutableMap<String, DomainDescriptor> domainDescriptorByName;

    public PlatformDescriptor(Map<String, DomainDescriptor> domainDescriptors){
        this.domainDescriptorByName = ImmutableMap.copyOf(domainDescriptors);
    }

    public ImmutableMap<String, DomainDescriptor> getDomainDescriptorByName() {
        return domainDescriptorByName;
    }
}
