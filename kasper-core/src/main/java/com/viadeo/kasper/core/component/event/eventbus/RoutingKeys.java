// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class RoutingKeys {

    private final Set<RoutingKey> routingKeys;
    private final Set<RoutingKey> deprecatedRoutingKeys;

    public RoutingKeys(Set<RoutingKey> routingKeys) {
        this.routingKeys = Sets.newHashSet();
        this.deprecatedRoutingKeys = Sets.newHashSet();

        for (final RoutingKey routingKey : routingKeys) {
            if (routingKey.isDeprecated()) {
                this.deprecatedRoutingKeys.add(routingKey);
            } else {
                this.routingKeys.add(routingKey);
            }
        }
    }

    public Set<RoutingKey> get() {
        return routingKeys;
    }

    public Set<RoutingKey> deprecated() {
        return deprecatedRoutingKeys;
    }

    public Set<RoutingKey> all() {
        final Set<RoutingKey> allRoutes = Sets.newHashSet(routingKeys);
        allRoutes.addAll(deprecatedRoutingKeys);
        return allRoutes;
    }

    public static class RoutingKey {

        private final String route;
        private final boolean deprecated;

        public RoutingKey(String route) {
            this(route, Boolean.FALSE);
        }

        public RoutingKey(String route, boolean deprecated) {
            this.route = checkNotNull(route);
            this.deprecated = checkNotNull(deprecated);
        }

        public String getRoute() {
            return route;
        }

        public boolean isDeprecated() {
            return deprecated;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(route, deprecated);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final RoutingKey other = (RoutingKey) obj;
            return Objects.equal(this.route, other.route) && Objects.equal(this.deprecated, other.deprecated);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("route", route)
                    .add("deprecated", deprecated)
                    .toString();
        }
    }
}
