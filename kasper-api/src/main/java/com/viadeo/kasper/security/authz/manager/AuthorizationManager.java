// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.manager;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.security.annotation.CombinesWith;

/**
 * An AuthorizationManager performs authorization (access control) operations for any given actor.
 * Each functions need the authorizations lists coming from {@link com.viadeo.kasper.security.annotation.XKasperRequiresPermissions} or
 * {@link com.viadeo.kasper.security.annotation.XKasperRequiresRoles} annotations, a {@link com.viadeo.kasper.security.annotation.CombinesWith} specifying
 * the operation (default "AND"), the current actor and an optional targetId (to which the permission holder) indicated by the
 * {@link com.viadeo.kasper.security.annotation.XKasperAuthorizationTargetId} annotation.
 *
 * @see com.viadeo.kasper.security.annotation.XKasperRequiresPermissions
 * @see com.viadeo.kasper.security.annotation.XKasperRequiresRoles
 * @see com.viadeo.kasper.security.annotation.CombinesWith
 * @see com.viadeo.kasper.security.annotation.XKasperAuthorizationTargetId
 */
public interface AuthorizationManager {

    boolean isPermitted(String[] permissions, CombinesWith combinesWith, ID actorID, Optional targetId);

    boolean hasRole(String[] roles, CombinesWith combinesWith, ID actorID, Optional targetId);

}
