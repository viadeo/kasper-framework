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
 * Value contains the list of roles to be tested.
 * Manager specifies the {@link com.viadeo.kasper.security.authz.manager.AuthorizationManager} to use in order to check given authorizations.
 * CombinesWith indicates the operation to do between authorizations.
 *
 * <p>For example:
 * <p/>
 * <code>&#64;XKasperRequiresRoles( value = "admin",  manager = DefaultAuthorizationSecurityManager.class , combinesWith = CombinesWith.OR )<br/>
 * public class GetExampleQueryHandler extends QueryHandler</code>
 * <p/>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperRequiresRoles {

    /**
     * the roles which be passed to the check method.
     */
    String[] value() default {};

    Class<? extends AuthorizationManager> manager() default AuthorizationManager.class;

    CombinesWith combinesWith() default CombinesWith.AND;


}