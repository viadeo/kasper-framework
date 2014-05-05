package com.viadeo.kasper.security.authz.mgt;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public interface AuthorizationSecurityManager {

    public Subject getSubject(final Context context);

    public Permission resolvePermission(String permission);

    public void checkRole(final String role, final Subject subject) throws KasperUnauthorizedException;

    public void checkRoles(List<String> roles, final Subject subject) throws KasperUnauthorizedException;

    public void checkPermission(final String perm, final Subject subject) throws KasperUnauthorizedException;

    public void checkPermissions(final List<String> permissions, final Subject subject) throws KasperUnauthorizedException;

}
