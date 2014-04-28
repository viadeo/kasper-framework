package com.viadeo.kasper.security.authz;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public interface AuthorizationSecurityManager {

    public boolean hasRole(final Role role, final Subject subject);

    public Subject getSubject(final Context context);

    public boolean isPermitted(Permission p, List<Permission> permissions);

    public Permission resolvePermission(String permission);

    public void checkRole(final String role, final Subject subject) throws KasperUnauthorizedException;

    public void checkRoles(List<String> roles, final Subject subject) throws KasperUnauthorizedException;

    public void checkPermission(final String perm, final Subject subject) throws KasperUnauthorizedException;

    public void checkPermissions(final List<String> permissions, final Subject subject) throws KasperUnauthorizedException;

}
