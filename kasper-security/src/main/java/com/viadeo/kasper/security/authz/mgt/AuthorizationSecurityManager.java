// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.mgt;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public interface AuthorizationSecurityManager {

    Permission resolvePermission(String permission);

    void checkRole(String role, Actor actor) throws KasperUnauthorizedException;

    void checkRoles(List<String> roles, Actor actor) throws KasperUnauthorizedException;

    void checkPermission(String perm, Actor actor) throws KasperUnauthorizedException;

    void checkPermissions(List<String> permissions, Actor actor) throws KasperUnauthorizedException;

}
