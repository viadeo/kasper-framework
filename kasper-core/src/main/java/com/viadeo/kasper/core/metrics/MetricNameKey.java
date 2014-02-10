// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.google.common.base.Objects;

public class MetricNameKey {
    private final MetricNameStyle kind;
    private final Class clazz;

    public MetricNameKey(final MetricNameStyle kind, final Class clazz) {
        this.kind = kind;
        this.clazz = clazz;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(kind, clazz);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MetricNameKey other = (MetricNameKey) obj;
        return Objects.equal(this.kind, other.kind) && Objects.equal(this.clazz, other.clazz);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("kind", kind)
                .add("clazz", clazz)
                .toString();
    }
}
