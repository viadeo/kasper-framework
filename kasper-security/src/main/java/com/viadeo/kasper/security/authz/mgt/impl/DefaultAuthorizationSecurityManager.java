// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultAuthorizationSecurityManager implements AuthorizationSecurityManager {

    public Permission resolvePermission(final String permission) {
        return new WildcardPermission(checkNotNull(permission));
    }

    // TODO
    public Subject getSubject(final Context context) {
        return new Subject();
    }

    public void checkRole(final String role, final Subject subject) throws KasperUnauthorizedException {
        if ((null == subject) || ( ! subject.hasRole(new Role(role)))) {
            throw new KasperUnauthorizedException(
                    "Unauthorized. Needed role : " + role,
                    CoreReasonCode.REQUIRE_AUTHORIZATION
            );
        }
    }

    public void checkRoles(final List<String> roles, final Subject subject) throws KasperUnauthorizedException {
        if ((null != roles) && ( ! roles.isEmpty()) && (null != subject)) {
            for (final String role : roles) {
                checkRole(role, subject);
            }
        }
    }

    public void checkPermission(final String perm, final Subject subject) throws KasperUnauthorizedException {

        final Permission permission = resolvePermission(perm);
        if ( ! subject.isPermitted(permission)) {
            throw new KasperUnauthorizedException(
                    "Unauthorized. Needed permission : " + permission,
                    CoreReasonCode.REQUIRE_AUTHORIZATION
            );
        }
    }

    public void checkPermissions(final List<String> permissions, final Subject subject) throws KasperUnauthorizedException {
        if ((null != permissions) && ( ! permissions.isEmpty()) && (null != subject)) {
            for (final String permission : permissions) {
                checkPermission(permission, subject);
            }
        }
    }
}
