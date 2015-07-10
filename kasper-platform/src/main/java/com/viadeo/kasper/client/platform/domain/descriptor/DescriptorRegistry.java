// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain.descriptor;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DescriptorRegistry implements Iterable<DomainDescriptor> {

    private final List<DomainDescriptor> descriptors;

    public DescriptorRegistry(final List<DomainDescriptor> descriptors) {
        this.descriptors = checkNotNull(descriptors);
    }

    @Override
    public Iterator<DomainDescriptor> iterator() {
        return descriptors.iterator();
    }

}
