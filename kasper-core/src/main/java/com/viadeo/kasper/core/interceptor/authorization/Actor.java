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
package com.viadeo.kasper.core.interceptor.authorization;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.ID;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Actor {

    private Set<Role> roles;
    private Set<Permission> permissions;

    // ------------------------------------------------------------------------
    public Actor(final ID actorID) {
        ID actorID1 = checkNotNull(actorID);
        this.roles = Sets.newHashSet();
        this.permissions = Sets.newHashSet();
    }

    // -----------------------------------------------------------------------
    public Set<Role> getRoles() {
        return roles;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    // ------------------------------------------------------------------------

    public void setRoles(final Set<Role> roles) {
        this.roles = checkNotNull(roles);
    }

    public void addRoles(final Set<Role> roles) {
        this.roles.addAll(checkNotNull(roles));
    }

    public void setPermissions(final Set<Permission> permissions) {
        this.permissions = checkNotNull(permissions);
    }

    public void addPermissions(final Collection<Permission> permissions) {
        this.permissions.addAll(checkNotNull(permissions));
    }

    public void removePermissions(final Collection<Permission> permissions) {
        this.permissions.removeAll(checkNotNull(permissions));
    }

    // ------------------------------------------------------------------------

    public boolean isPermitted(final Permission p) {
        checkNotNull(p);
        if ((null != this.permissions) && (!this.permissions.isEmpty())) {
            for (final Permission perm : this.permissions) {
                if (perm.implies(p)) {
                    return true;
                }
            }
        }
        if ((null != this.roles) && (!this.roles.isEmpty())) {
            for (final Role role : this.roles) {
                if (role.isPermitted(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasRole(final Role role) {
        return this.roles.contains(checkNotNull(role));
    }
}

