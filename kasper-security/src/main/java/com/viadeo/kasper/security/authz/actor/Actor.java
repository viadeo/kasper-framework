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

package com.viadeo.kasper.security.authz.actor;


import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Actor {

    private List<Role> roles;
    private List<Permission> permissions;
    private KasperID kasperId;

    // ------------------------------------------------------------------------

    public Actor() {
        this.roles = Lists.newArrayList();
        this.permissions = Lists.newArrayList();
        this.kasperId = new DefaultKasperId();
    }

    public Actor(final KasperID kasperId) {
        this.roles = Lists.newArrayList();
        this.permissions = Lists.newArrayList();
        this.kasperId = checkNotNull(kasperId);
    }

    public Actor(final KasperID kasperId, final List<Role> roles, final List<Permission> permissions) {
        this.roles = checkNotNull(roles);
        this.permissions = checkNotNull(permissions);
        this.kasperId = checkNotNull(kasperId);
    }

    // ------------------------------------------------------------------------

    public KasperID getKasperId() {
        return kasperId;
    }

    public void setKasperId(KasperID kasperId) {
        this.kasperId = kasperId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = checkNotNull(roles);
    }

    public void addRole(final Role role) {
        this.roles.add(checkNotNull(role));
    }

    public void removeRole(final Role role) {
        this.roles.remove(checkNotNull(role));
    }

    public void addRoles(final Collection<Role> roles){
        this.roles.addAll(checkNotNull(roles));
    }

    public void removeRoles(final Collection<Role> roles){
        this.roles.removeAll(checkNotNull(roles));
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<Permission> permissions) {
        this.permissions = checkNotNull(permissions);
    }

    public void addPermission(final Permission permission) {
        this.permissions.add(checkNotNull(permission));
    }

    public void removePermission(final Permission permission) {
        this.permissions.remove(checkNotNull(permission));
    }

    public void addPermissions(final Collection<Permission> permissions) {
        this.permissions.addAll(checkNotNull(permissions));
    }

    public void removePermissions(final Collection<Permission> permissions) {
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

        if (this.permissions != null && ( ! this.permissions.isEmpty())) {
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
