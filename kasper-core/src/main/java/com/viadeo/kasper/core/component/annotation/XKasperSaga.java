// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
    @interface Start {

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
    @interface End {

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
    @interface Step {

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
    @interface Schedule {

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

        /**
         *
         * @return true if the saga should end immediately after scheduled method execution (same as EndStep)
         */
        boolean end() default false;
    }

    // ------------------------------------------------------------------------

    /**
     * The <code>ScheduledByEvent</code> annotation is used to schedule a method invocation when the related step is triggered.
     * The annotation must be used in addition to the following annotations : <code>Start</code>, <code>Step</code>. and <code>End</code>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface ScheduledByEvent {

        /**
         * @return the method name to be invoked
         */
        String methodName();

        /**
         *
         * @return if the saga should end immediately after scheduled method execution (same as EndStep)
         */
        boolean end() default false;
    }

    // ------------------------------------------------------------------------

    /**
     * The <code>CancelSchedule</code> annotation is used to schedule a method invocation when the related step is triggered.
     * The annotation must be used in addition to the following annotations : <code>Start</code>, <code>Step</code>. and <code>End</code>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface CancelSchedule {

        /**
         * @return the method name for which we have a scheduled invocation
         */
        String methodName();
    }

}
