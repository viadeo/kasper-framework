// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hashCode(descriptors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DescriptorRegistry other = (DescriptorRegistry) obj;
        return Objects.equal(this.descriptors, other.descriptors);
    }
}
