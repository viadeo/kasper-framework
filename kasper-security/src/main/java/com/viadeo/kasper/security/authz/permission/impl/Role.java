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

// derived from apache shiro SimpleRole class

package com.viadeo.kasper.security.authz.permission.impl;


import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.permission.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Role {

    private String name;
    private List<Permission> permissions;

    // ------------------------------------------------------------------------

    public Role() {

    }


    public Role(final String name) {
        setName(checkNotNull(name));
    }

    public Role(final String name, final List<Permission> permissions) {
        setName(checkNotNull(name));
        setPermissions(checkNotNull(permissions));
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = checkNotNull(name);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<Permission> permissions) {
        this.permissions = checkNotNull(permissions);
    }

    // ------------------------------------------------------------------------

    public void add(final Permission permission) {
        checkNotNull(permission);
        if (null == this.permissions) {
            this.permissions = Lists.newArrayList();
        }
        this.permissions.add(permission);
    }

    public void remove(final Permission permission) {
        checkNotNull(permission);
        if (null != this.permissions) {
            this.permissions.remove(permission);
        }
    }

    public void addAll(final Collection<Permission> perms) {
        if ((null != perms) && (!perms.isEmpty())) {
            if (null == this.permissions) {
                this.permissions = new ArrayList<Permission>(perms.size());
            }
            this.permissions.addAll(perms);
        }
    }

    public void removeAll(final Collection<Permission> perms) {
        if ((null != perms) && (!perms.isEmpty())) {
            if (null != this.permissions) {
                permissions.removeAll(perms);
                ;
            }
        }
    }

    // ------------------------------------------------------------------------

    public boolean isPermitted(final Permission p) {
        if ((null != this.permissions) && (!this.permissions.isEmpty())) {
            for (final Permission perm : this.permissions) {
                if (perm.implies(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------

    public int hashCode() {
        return ((null != getName()) ? getName().hashCode() : 0);
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Role) {
            final Role sr = (Role) o;
            /* only check name, since role names should be unique across an entire application: */
            return (getName() != null ? getName().equalsIgnoreCase(sr.getName()) : sr.getName() == null);
        }
        return false;
    }

    public String toString() {
        return getName();
    }

}
