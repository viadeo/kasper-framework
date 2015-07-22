// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;
import com.viadeo.kasper.core.component.command.repository.Repository;

import static com.google.common.base.Preconditions.checkNotNull;

public class RepositoryDescriptor implements KasperComponentDescriptor {

    private final Class<? extends Repository> repositoryClass;
    private final AggregateDescriptor aggregateDescriptor;

    // ------------------------------------------------------------------------

    public RepositoryDescriptor(final Class<? extends Repository> repositoryClass,
                                final AggregateDescriptor aggregateDescriptor) {
        this.repositoryClass = checkNotNull(repositoryClass);
        this.aggregateDescriptor = checkNotNull(aggregateDescriptor);
    }

    @Override
    public Class<? extends Repository> getReferenceClass() {
        return repositoryClass;
    }

    public AggregateDescriptor getAggregateDescriptor() {
        return aggregateDescriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(repositoryClass, aggregateDescriptor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RepositoryDescriptor other = (RepositoryDescriptor) obj;
        return Objects.equal(this.repositoryClass, other.repositoryClass) && Objects.equal(this.aggregateDescriptor, other.aggregateDescriptor);
    }
}
