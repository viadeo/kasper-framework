// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.mgt;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public interface AuthorizationSecurityManager {


    Subject getSubject(Context context);

    Permission resolvePermission(String permission);

    void checkRole(String role, Subject subject) throws KasperUnauthorizedException;

    void checkRoles(List<String> roles, Subject subject) throws KasperUnauthorizedException;

    void checkPermission(String perm, Subject subject) throws KasperUnauthorizedException;

    void checkPermissions(List<String> permissions, Subject subject) throws KasperUnauthorizedException;

}
