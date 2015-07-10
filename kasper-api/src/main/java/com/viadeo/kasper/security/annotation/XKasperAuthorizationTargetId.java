// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the id on which the authorization holder.
 * It has to be placed on a command or a query field
 *
 * For example:
 *
 * # @XKasperQuery
 * # public class GetExampleQuery implements Query {
 * #     @XKasperAuthorizationTargetId
 * #     private final String exampleId;
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XKasperAuthorizationTargetId {


}
