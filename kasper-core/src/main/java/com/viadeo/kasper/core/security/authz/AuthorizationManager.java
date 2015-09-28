// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authz;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;

/**
 * An AuthorizationManager performs authorization (access control) operations for any given actor.
 * Each functions need the authorizations lists coming from {@link com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresPermissions} or
 * {@link com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresRoles} annotations, a {@link CombinesWith} specifying
 * the operation (default "AND"), the current actor and an optional targetId (to which the permission holder) indicated by the
 * {@link com.viadeo.kasper.api.annotation.XKasperAuthz.TargetId} annotation.
 *
 * @see com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresPermissions
 * @see com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresRoles
 * @see CombinesWith
 * @see com.viadeo.kasper.api.annotation.XKasperAuthz.TargetId
 */
public interface AuthorizationManager {

    boolean isPermitted(String[] permissions, CombinesWith combinesWith, ID actorID, Optional targetId);

    boolean hasRole(String[] roles, CombinesWith combinesWith, ID actorID, Optional targetId);

}
