// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.annotation;

import com.viadeo.kasper.api.component.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 *  The <code>XKasperSaga</code> annotation specifies some meta information of a saga.
 *  In more it provide a set of annotations allowing to define a <code>Saga</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperSaga {

    /**
     * @return the event listener's description
     */
    String description() default "";

    /**
     * @return the domain of this command handler
     */
    Class<? extends Domain> domain();


    // ------------------------------------------------------------------------

    /**
     * The <code>Start</code> annotation is used to declare a start step into the life cycle of a saga.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Start {

        /**
         * Indicate the method name of the event for which we get the saga identifier.
         * @return the saga identifier
         */
        String getter();

    }

    // ------------------------------------------------------------------------

    /**
     * The <code>End</code> annotation is used to declare an end step into the life cycle of a saga.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface End {

        /**
         * Indicate the method name of the event for which we get the saga identifier.
         * @return the saga identifier
         */
        String getter();

    }

    // ------------------------------------------------------------------------

    /**
     * The <code>Step</code> annotation is used to declare a basic step.
     * This step has no impact on the life cycle of a saga.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Step {

        /**
         * Indicate the method name of the event for which we get the saga identifier.
         * @return the saga identifier
         */
        String getter();

    }

    // ------------------------------------------------------------------------

    /**
     * The <code>Schedule</code> annotation is used to schedule a method invocation when the related step is triggered.
     * The annotation must be used in addition to the following annotations : <code>Start</code>, <code>Step</code>. and <code>End</code>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Schedule {

        /**
         * @return the delay to invoke the method
         */
        long delay();

        /**
         *
         * @return the unit of the delay
         */
        TimeUnit unit();

        /**
         * @return the method name to be invoked
         */
        String methodName();
    }

    // ------------------------------------------------------------------------

    /**
     * The <code>ScheduledByEvent</code> annotation is used to schedule a method invocation when the related step is triggered.
     * The annotation must be used in addition to the following annotations : <code>Start</code>, <code>Step</code>. and <code>End</code>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ScheduledByEvent {

        /**
         * @return the method name to be invoked
         */
        String methodName();
    }

    // ------------------------------------------------------------------------

    /**
     * The <code>CancelSchedule</code> annotation is used to schedule a method invocation when the related step is triggered.
     * The annotation must be used in addition to the following annotations : <code>Start</code>, <code>Step</code>. and <code>End</code>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface CancelSchedule {

        /**
         * @return the method name for which we have a scheduled invocation
         */
        String methodName();
    }

}
