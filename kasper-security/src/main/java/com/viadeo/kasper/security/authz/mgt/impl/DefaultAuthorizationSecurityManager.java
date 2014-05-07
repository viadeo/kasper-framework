package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public class DefaultAuthorizationSecurityManager implements AuthorizationSecurityManager {

    public Permission resolvePermission(String permission) {
        return new WildcardPermission(permission);
    }

    //TODO
    public Subject getSubject(final Context context) {
        return new Subject();
    }

    public void addRole(final Role role, final Subject subject){
        subject.addRole(role);
    }

    public void removeRole(final Role role, final Subject subject){
        subject.removeRole(role);
    }

    public void addPermission(final Permission permission, final Subject subject){
        subject.addPermission(permission);
    }

    public void removePermission(final Permission permission, final Subject subject){
        subject.removePermission(permission);
    }

    public void addRoles(final List<Role> roles, final Subject subject){
        subject.addRoles(roles);
    }

    public void removeRoles(final List<Role> roles, final Subject subject){
        subject.removeRoles(roles);
    }

    public void addPermissions(final List<Permission> permissions, final Subject subject){
        subject.addPermissions(permissions);
    }

    public void removePermissions(final List<Permission> permissions, final Subject subject){
        subject.removePermissions(permissions);
    }

    public void checkRole(final String role, final Subject subject) throws KasperUnauthorizedException {
        if (subject == null || !subject.hasRole(new Role(role))) {
            throw new KasperUnauthorizedException("Unauthorized. Needed role : " + role, CoreReasonCode.REQUIRE_AUTHORIZATION);
        }
    }

    public void checkRoles(List<String> roles, final Subject subject) throws KasperUnauthorizedException {
        if (roles != null && !roles.isEmpty() && subject != null) {
            for (String role : roles) {
                checkRole(role, subject);
            }
        }
    }

    public void checkPermission(final String perm, final Subject subject) throws KasperUnauthorizedException {
        Permission permission = resolvePermission(perm);
        if (!subject.isPermitted(permission)) {
            throw new KasperUnauthorizedException("Unauthorized. Needed permission : " + permission, CoreReasonCode.REQUIRE_AUTHORIZATION);
        }
    }

    public void checkPermissions(final List<String> permissions, final Subject subject) throws KasperUnauthorizedException {
        if (permissions != null && !permissions.isEmpty() && subject != null) {
            for (String permission : permissions) {
                checkPermission(permission, subject);
            }
        }
    }
}
