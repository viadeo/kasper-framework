// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.annotation;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface XKasperAuthz {

    /**
     * An enum for specifying a combinesWith operation that can be used for interpreting authorization annotations
     */
    public enum CombinesWith {
        AND, OR
    }

    // ------------------------------------------------------------------------

    /**
     * An AuthorizationManager performs authorization (access control) operations for any given actor.
     * Each functions need the authorizations lists coming from {@link com.viadeo.kasper.api.annotation.XKasperAuthz.RequiresPermissions} or
     * {@link com.viadeo.kasper.api.annotation.XKasperAuthz.RequiresRoles} annotations, a {@link com.viadeo.kasper.api.annotation.XKasperAuthz.CombinesWith} specifying
     * the operation (default "AND"), the current actor and an optional targetId (to which the permission holder) indicated by the
     * {@link com.viadeo.kasper.api.annotation.XKasperAuthz.TargetId} annotation.
     *
     * @see com.viadeo.kasper.api.annotation.XKasperAuthz.RequiresPermissions
     * @see com.viadeo.kasper.api.annotation.XKasperAuthz.RequiresRoles
     * @see com.viadeo.kasper.api.annotation.XKasperAuthz.CombinesWith
     * @see com.viadeo.kasper.api.annotation.XKasperAuthz.TargetId
     */
    public interface AuthorizationManager {

        boolean isPermitted(String[] permissions, CombinesWith combinesWith, ID actorID, Optional targetId);

        boolean hasRole(String[] roles, CombinesWith combinesWith, ID actorID, Optional targetId);

    }

    // ------------------------------------------------------------------------

    /**
     *
     * Indicates the id on which the authorization holder.
     * It has to be placed on a command or a query field
     *
     * <p>For example:</p>
     * <code>
     * &#64;XKasperQuery
     * public class GetExampleQuery implements Query {
     * &#64;XKasperAuthorizationTargetId
     * private final String exampleId;
     * </code>
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface TargetId { }

    // ------------------------------------------------------------------------

    /**
     * Requires a user in context to imply a particular permission in order to execute the annotated Handler.
     * Value contains the list of permissions to be tested.
     * Manager specifies the {@link AuthorizationManager} to use in order to check given authorizations.
     * CombinesWith indicates the operation to do between authorizations.
     *
     * <p>For example:</p>
     * <code>
     * &#64;XKasperRequiresPermissions( value = "read",  manager = DefaultAuthorizationSecurityManager.class , combinesWith = CombinesWith.OR )
     * public class GetExampleQueryHandler extends QueryHandler
     * </code>
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresPermissions {

        /**
         * the permissions which be passed to the check method.
         * @return permissions
         */
        String[] value() default {};

        Class<? extends AuthorizationManager> manager() default AuthorizationManager.class;

        CombinesWith combinesWith() default CombinesWith.AND;

    }

    // ------------------------------------------------------------------------

    /**
     * Requires a user in context to imply a particular permission in order to execute the annotated Handler.
     * Value contains the list of roles to be tested.
     * Manager specifies the {@link AuthorizationManager} to use in order to check given authorizations.
     * CombinesWith indicates the operation to do between authorizations.
     *
     * <p>For example: </p>
     * <code>
     * &#64;XKasperRequiresRoles( value = "admin",  manager = DefaultAuthorizationSecurityManager.class , combinesWith = CombinesWith.OR )
     * public class GetExampleQueryHandler extends QueryHandler
     * </code>
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresRoles {

        /**
         * the roles which be passed to the check method.
         * @return roles
         */
        String[] value() default {};

        Class<? extends AuthorizationManager> manager() default AuthorizationManager.class;

        CombinesWith combinesWith() default CombinesWith.AND;

    }
}