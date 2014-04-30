package com.viadeo.kasper.security.authz.subject;

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

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> resolvePermissionsInRole() {
        List<Permission> permissions = new ArrayList<Permission>();
        for (Role role : getRoles()) {
            permissions.addAll(role.getPermissions());
        }
        return permissions;
    }
}
