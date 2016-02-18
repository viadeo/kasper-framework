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

