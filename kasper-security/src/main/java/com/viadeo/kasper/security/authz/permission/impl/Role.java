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

import com.viadeo.kasper.security.authz.permission.Permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Role {

    private String name;
    private List<Permission> permissions;


    public Role() {
    }

    public Role(String name) {
        setName(name);
    }

    public Role(String name, List<Permission> permissions) {
        setName(name);
        setPermissions(permissions);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void add(Permission permission) {
        List<Permission> permissions = getPermissions();
        if (permissions == null) {
            permissions = new ArrayList<Permission>();
            setPermissions(permissions);
        }
        permissions.add(permission);
    }

    public void addAll(Collection<Permission> perms) {
        if (perms != null && !perms.isEmpty()) {
            List<Permission> permissions = getPermissions();
            if (permissions == null) {
                permissions = new ArrayList<Permission>(perms.size());
                setPermissions(permissions);
            }
            permissions.addAll(perms);
        }
    }

    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Role) {
            Role sr = (Role) o;
            //only check name, since role names should be unique across an entire application:
            return (getName() != null ? getName().equals(sr.getName()) : sr.getName() == null);
        }
        return false;
    }

    public String toString() {
        return getName();
    }
}
