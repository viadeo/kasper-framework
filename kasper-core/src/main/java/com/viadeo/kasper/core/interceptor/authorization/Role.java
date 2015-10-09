/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// inspired from apache shiro

package com.viadeo.kasper.core.interceptor.authorization;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Role {

    private DefaultKasperId kasperId;
    private String name;
    private Set<Permission> permissions;
    private Optional targetId;

    // ------------------------------------------------------------------------

    public Role(final String name) {
        this(new DefaultKasperId(), name, Sets.<Permission>newHashSet(), Optional.absent());
    }

    public Role(final String name, final Optional targetId) {
        this(new DefaultKasperId(), name, Sets.<Permission>newHashSet(), targetId);
    }

    public Role(final DefaultKasperId kasperId, final String name, final Optional targetId) {
        this(kasperId, name, Sets.<Permission>newHashSet(), targetId);
    }

    public Role(final DefaultKasperId kasperId, final String name, final Set<Permission> permissions, final Optional targetId) {
        this.kasperId = checkNotNull(kasperId);
        this.name = checkNotNull(name);
        this.permissions = checkNotNull(permissions);
        this.targetId = checkNotNull(targetId);
    }

    public KasperID getId() {
        return kasperId;
    }

    public void setId(DefaultKasperId kasperId) {
        this.kasperId = checkNotNull(kasperId);
    }

    public String getName() {
        return name;
    }

    protected void setName(final String name) {
        this.name = checkNotNull(name);
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final Set<Permission> permissions) {
        this.permissions = checkNotNull(permissions);
    }

    public Optional getTargetId() {
        return targetId;
    }

    public void setTargetId(Optional targetId) {
        this.targetId = targetId;
    }

    // ------------------------------------------------------------------------

    public void add(final Permission permission) {
        checkNotNull(permission);
        if (null == this.permissions) {
            this.permissions = Sets.newHashSet();
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
                this.permissions = Sets.newHashSetWithExpectedSize(perms.size());
            }
            this.permissions.addAll(perms);
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
            boolean isEquals = getName() != null ? getName().equalsIgnoreCase(sr.getName()) : sr.getName() == null;
            if(isEquals && sr.targetId.isPresent() && (!this.targetId.isPresent() || !this.targetId.equals(sr.targetId))){
                isEquals = false;
            }
            return isEquals;
        }
        return false;
    }

    public String toString() {
        return getName();
    }


}