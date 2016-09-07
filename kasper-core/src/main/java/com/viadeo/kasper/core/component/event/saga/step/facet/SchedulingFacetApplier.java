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
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.google.common.collect.Sets;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.saga.step.Scheduler;
import com.viadeo.kasper.core.component.event.saga.step.Step;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SchedulingFacetApplier implements FacetApplier {

    private final Scheduler scheduler;

    public SchedulingFacetApplier(final Scheduler scheduler) {
        this.scheduler = checkNotNull(scheduler);
    }

    // ------------------------------------------------------------------------

    @Override
    public Step apply(final Method method, final Step step) {
        final XKasperSaga.Schedule scheduleAnnotation = method.getAnnotation(XKasperSaga.Schedule.class);
        final XKasperSaga.CancelSchedule cancelScheduleAnnotation = method.getAnnotation(XKasperSaga.CancelSchedule.class);
        final XKasperSaga.ScheduledByEvent scheduledByEventAnnotation = method.getAnnotation(XKasperSaga.ScheduledByEvent.class);

        final Set<Annotation> annotations = Sets.newHashSet();

        if (scheduleAnnotation != null) {
            annotations.add(scheduleAnnotation);
        }

        if (cancelScheduleAnnotation != null) {
            annotations.add(cancelScheduleAnnotation);
        }

        if (scheduledByEventAnnotation != null) {
            annotations.add(scheduledByEventAnnotation);
        }

        if (annotations.size() > 1) {
            throw new IllegalStateException(String.format("Should have one schedule annotation per step : %s", step.getSagaClass().getName()));
        }

        if (null != scheduleAnnotation) {
            return new SchedulingStep(scheduler, step, scheduleAnnotation);
        }

        if (null != cancelScheduleAnnotation) {
            return new SchedulingStep(scheduler, step, cancelScheduleAnnotation);
        }

        if (null != scheduledByEventAnnotation) {
            return new SchedulingStep(scheduler, step, scheduledByEventAnnotation);
        }

        return step;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

}
