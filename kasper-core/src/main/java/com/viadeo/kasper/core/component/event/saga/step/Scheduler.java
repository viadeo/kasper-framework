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
package com.viadeo.kasper.core.component.event.saga.step;

import com.viadeo.kasper.core.component.event.saga.Saga;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.quartz.SchedulerException;

/**
 * Interface describing the scheduler Step of a Saga
 */
public interface Scheduler {

    /**
     * initialize the <code>Scheduler</code>
     */
    void initialize();

    /**
     * shutdown the <code>Scheduler</code>
     *
     * @throws SchedulerException if an error occurs during the shutdown
     */
    void shutdown() throws SchedulerException;

    /**
     * indicates if the scheduler is initialized
     * @return true if the scheduler is initialized, false otherwise
     */
    boolean isInitialized();

    /**
     * called in order to trigger a particular Saga method invocation
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     * @param triggerDuration the delay at which the method will be triggered
     * @param endAfterExecution is the saga should end after scheduled method execution
     * @return the jobIdentifier of the scheduled operation
     */
    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, Duration triggerDuration, boolean endAfterExecution);

    /**
     * called in order to trigger a particular Saga method invocation
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     * @param triggerDateTime the time at which the method will be triggered
     * @param endAfterExecution is the saga should end after scheduled method execution
     * @return the jobIdentifier of the scheduled operation
     */
    String schedule(Class<? extends Saga> sagaClass, String methodName, Object identifier, DateTime triggerDateTime, boolean endAfterExecution);

    /**
     * called to cancel a scheduled operation
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     */
    void cancelSchedule(Class<? extends Saga> sagaClass, String methodName, Object identifier);

    /**
     * indicates if a particular method invocation is already scheduled.
     *
     * @param sagaClass the saga class
     * @param methodName the method name
     * @param identifier the saga identifier
     * @return true if scheduled
     */
    boolean isScheduled(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier);

}
