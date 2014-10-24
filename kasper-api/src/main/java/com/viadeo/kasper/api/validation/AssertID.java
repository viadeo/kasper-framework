// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.validation;

import com.viadeo.kasper.api.validation.validator.AssertIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {AssertIDValidator.class})
public @interface AssertID {
    String message() default "Unexpected ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String vendor() default "";
    String[] type() default {};
    String format() default "";
}