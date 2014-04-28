package com.viadeo.kasper.security.authz.impl;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.Permission;
import com.viadeo.kasper.security.authz.Role;
import com.viadeo.kasper.security.authz.Subject;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public class DefaultAuthorizationSecurityManager implements AuthorizationSecurityManager {

    public Permission resolvePermission(String permission) {
        return new Permission(permission);
    }

    //TODO
    public Subject getSubject(Context context) {
        return new Subject();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isPermitted(Permission p, List<Permission> permissions) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission perm : permissions) {
                if (perm.implies(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasRole(Role role, Subject subject) {
        return subject.getRoles().contains(role);
    }

    public void checkRole(final String role, final Subject subject) throws KasperUnauthorizedException {
        if (!hasRole(new Role(role), subject)) {
            throw new KasperUnauthorizedException("Unauthorized. Need role : " + role, CoreReasonCode.REQUIRE_AUTHORIZATION);
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
        if (!(isPermitted(permission, subject.getPermissions())
                && isPermitted(permission, subject.resolvePermissionsInRole()))) {
            throw new KasperUnauthorizedException("Unauthorized. Need permission : " + permission, CoreReasonCode.REQUIRE_AUTHORIZATION);
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
