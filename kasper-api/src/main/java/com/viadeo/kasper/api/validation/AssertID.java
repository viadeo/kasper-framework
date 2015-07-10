// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.validation;

import com.viadeo.kasper.api.validation.validator.CollectionOfIDValidator;
import com.viadeo.kasper.api.validation.validator.IDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must respect some additional constraints according to the specified parameters.
 *
 *   Additional constraints are on :
 *      - vendor
 *      - type
 *      - format
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IDValidator.class, CollectionOfIDValidator.class})
public @interface AssertID {
    String message() default "Unexpected ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * @return vendor if specified then the element must be equal to
     */
    String vendor() default "";

    /**
     * @return type if specified then the element must be equal to one of
     */
    String[] type() default {};

    /**
     * @return format if specified then the element must be equal to
     */
    String format() default "";
}
