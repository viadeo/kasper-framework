// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// inspired from apache shiro

package com.viadeo.kasper.security.authz.entities.actor;


import com.google.common.collect.Lists;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Actor extends Concept {

    private List<Role> roles = Lists.newArrayList();
    private List<WildcardPermission> permissions = Lists.newArrayList();

    // ------------------------------------------------------------------------

    public Actor() { }

    // -----------------------------------------------------------------------

    public List<Role> getRoles() {
        return roles;
    }

    public List<WildcardPermission> getPermissions() {
        return permissions;
    }

    // ------------------------------------------------------------------------

    protected void setRoles(final List<Role> roles) {
        this.roles = checkNotNull(roles);
    }

    protected void addRole(final Role role) {
        this.roles.add(checkNotNull(role));
    }

    protected void removeRole(final Role role) {
        this.roles.remove(checkNotNull(role));
    }

    protected void addRoles(final Collection<Role> roles){
        this.roles.addAll(checkNotNull(roles));
    }

    protected void removeRoles(final Collection<Role> roles){
        this.roles.removeAll(checkNotNull(roles));
    }

    protected void setPermissions(final List<WildcardPermission> permissions) {
        this.permissions = checkNotNull(permissions);
    }

    protected void addPermission(final WildcardPermission permission) {
        this.permissions.add(checkNotNull(permission));
    }

    protected void removePermission(final WildcardPermission permission) {
        this.permissions.remove(checkNotNull(permission));
    }

    protected void addPermissions(final Collection<WildcardPermission> permissions) {
        this.permissions.addAll(checkNotNull(permissions));
    }

    protected void removePermissions(final Collection<WildcardPermission> permissions) {
        this.permissions.removeAll(checkNotNull(permissions));
    }

    // ------------------------------------------------------------------------

    public List<Permission> resolvePermissionsInRole() {
        // FIXME: cache this list instead of recreating it each time
        final List<Permission> permissions = new ArrayList<Permission>();
        for (final Role role : getRoles()) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }

    public boolean isPermitted(final Permission p) {
        checkNotNull(p);

        if ((null != this.permissions) && ( ! this.permissions.isEmpty())) {
            for (final Permission perm : this.permissions) {
                if (perm.implies(p)) {
                    return true;
                }
            }
        }

        if ((null != this.roles) && ( ! this.roles.isEmpty())) {
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
