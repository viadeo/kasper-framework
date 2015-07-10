package com.viadeo.kasper.exposition.spring;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.viadeo.kasper.exposition.http.jetty.locators.Resource;

import java.util.List;

public class ResourceLocator {
    private final List<Resource> resourceList;

    public ResourceLocator() {
        this.resourceList = Lists.newArrayList();
    }

    public void registerResource(Resource resource) {
        this.resourceList.add(resource);
    }

    public List<Resource> listResources() {
        return ImmutableList.copyOf(resourceList);
    }
}
