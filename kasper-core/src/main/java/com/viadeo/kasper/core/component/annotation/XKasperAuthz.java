// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.annotation;

import com.viadeo.kasper.core.interceptor.authorization.AuthorizationManager;
import com.viadeo.kasper.core.interceptor.authorization.CombinesWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Indicate that the handler needs authorization to be executed
 *
 */
public interface XKasperAuthz {

    /**
     * Requires a user in context to imply a particular permission in order to execute the annotated Handler.
     * Value contains the list of permissions to be tested.
     * Manager specifies the {@link com.viadeo.kasper.core.interceptor.authorization.AuthorizationManager} to use in order to check given authorizations.
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
