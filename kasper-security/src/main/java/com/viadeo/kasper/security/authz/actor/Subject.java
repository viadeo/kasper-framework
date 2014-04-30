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

import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    private List<Role> roles;
    private List<Permission> permissions;

    public Subject() {
        this.roles = new ArrayList<Role>();
        this.permissions = new ArrayList<Permission>();
    }

    public Subject(final List<Role> roles, final List<Permission> permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(Permission permission){
        this.permissions.add(permission);
    }

    public List<Permission> resolvePermissionsInRole() {
        List<Permission> permissions = new ArrayList<Permission>();
        for (Role role : getRoles()) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }
}
