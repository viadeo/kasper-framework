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
package com.viadeo.kasper.spring.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.step.quartz.MethodInvocationScheduler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * MethodInvocationScheduler implementation that delegates scheduling and triggering to a Quartz Scheduler.
 */
public class MethodInvocationSpringScheduler extends MethodInvocationScheduler implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationSpringScheduler.class);

    // ------------------------------------------------------------------------

    public MethodInvocationSpringScheduler(final ObjectMapper mapper, final Scheduler scheduler, final SagaManager sagaManager) {
        this(mapper, scheduler, DEFAULT_GROUP_NAME, sagaManager);
    }

    public MethodInvocationSpringScheduler(
            final ObjectMapper mapper,
            final Scheduler scheduler,
            final String groupIdentifier,
            final SagaManager sagaManager
    ) {
        super(mapper, scheduler, groupIdentifier, sagaManager);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void start() {
        this.initialize();
    }

    @Override
    public void stop() {
        try {
            this.shutdown();
        } catch (SchedulerException e) {
            LOGGER.error("Failed to shutdown the scheduler", e);
        }
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public boolean isRunning() {
        return isInitialized();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

}
