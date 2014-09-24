// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.mgt;

import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public interface AuthorizationSecurityManager {

    Permission resolvePermission(final String permission);

    void checkRole(final String role, final Actor actor) throws KasperUnauthorizedException;

    void checkRoles(final List<String> roles, final Actor actor) throws KasperUnauthorizedException;

    void checkPermission(final String perm, final Actor actor) throws KasperUnauthorizedException;

    void checkPermissions(final List<String> permissions, final Actor actor) throws KasperUnauthorizedException;

}
