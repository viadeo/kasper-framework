// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.annotation;

import com.viadeo.kasper.security.authz.manager.AuthorizationManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a user in context to imply a particular permission in order to execute the annotated Handler.
 * Value contains the list of permissions to be tested.
 * Manager specifies the {@link com.viadeo.kasper.security.authz.manager.AuthorizationManager} to use in order to check given authorizations.
 * CombinesWith indicates the operation to do between authorizations.
 *
 * For example:
 *
 * # @XKasperRequiresPermissions( value = "read",  manager = DefaultAuthorizationSecurityManager.class , combinesWith = CombinesWith.OR )
 * # public class GetExampleQueryHandler extends QueryHandler {
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperRequiresPermissions {

    /**
     * the permissions which be passed to the check method.
     * @return an array of permissions
     */
    String[] value() default {};

    Class<? extends AuthorizationManager> manager() default AuthorizationManager.class;

    CombinesWith combinesWith() default CombinesWith.AND;

}
