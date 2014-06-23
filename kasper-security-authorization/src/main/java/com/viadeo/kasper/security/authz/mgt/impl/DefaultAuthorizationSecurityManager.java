// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultAuthorizationSecurityManager implements AuthorizationSecurityManager {

    public Permission resolvePermission(final String permission) {
        return new WildcardPermission(checkNotNull(permission));
    }

    public void checkRole(final String role, final Actor actor) throws KasperUnauthorizedException {
        if ((null == actor) || ( ! actor.hasRole(new Role(role)))) {
            throw new KasperUnauthorizedException(
                    "Unauthorized. Needed role : " + role,
                    CoreReasonCode.REQUIRE_AUTHORIZATION
            );
        }
    }

    public void checkRoles(final List<String> roles, final Actor actor) throws KasperUnauthorizedException {
        if ((null != roles) && ( ! roles.isEmpty()) && (null != actor)) {
            for (final String role : roles) {
                checkRole(role, actor);
            }
        }
    }

    public void checkPermission(final String perm, final Actor actor) throws KasperUnauthorizedException {

        final Permission permission = resolvePermission(perm);
        if ( ! actor.isPermitted(permission)) {
            throw new KasperUnauthorizedException(
                    "Unauthorized. Needed permission : " + permission,
                    CoreReasonCode.REQUIRE_AUTHORIZATION
            );
        }
    }

    public void checkPermissions(final List<String> permissions, final Actor actor) throws KasperUnauthorizedException {
        if ((null != permissions) && ( ! permissions.isEmpty()) && (null != actor)) {
            for (final String permission : permissions) {
                checkPermission(permission, actor);
            }
        }
    }
}
