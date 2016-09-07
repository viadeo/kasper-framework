// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
